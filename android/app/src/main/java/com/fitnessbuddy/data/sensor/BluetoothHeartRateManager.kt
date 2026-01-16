package com.fitnessbuddy.data.sensor

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Standard Bluetooth UUIDs for Heart Rate Service
 */
object HeartRateServiceUuids {
    // Heart Rate Service UUID
    val HEART_RATE_SERVICE: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
    // Heart Rate Measurement Characteristic UUID
    val HEART_RATE_MEASUREMENT: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    // Client Characteristic Configuration Descriptor UUID (for enabling notifications)
    val CLIENT_CONFIG_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
}

data class HeartRateSensorDevice(
    val name: String,
    val address: String,
    val device: BluetoothDevice
)

enum class HeartRateSensorState {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED
}

/**
 * Manager for Bluetooth Low Energy heart rate sensors.
 * Supports standard BLE heart rate monitors (Polar, Wahoo, Garmin, etc.)
 */
@Singleton
class BluetoothHeartRateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "BluetoothHRManager"
    
    private val bluetoothManager: BluetoothManager? = 
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    
    private var bluetoothGatt: BluetoothGatt? = null
    
    private val _sensorState = MutableStateFlow(HeartRateSensorState.DISCONNECTED)
    val sensorState: StateFlow<HeartRateSensorState> = _sensorState.asStateFlow()
    
    private val _currentHeartRate = MutableStateFlow<Int?>(null)
    val currentHeartRate: StateFlow<Int?> = _currentHeartRate.asStateFlow()
    
    private val _discoveredDevices = MutableStateFlow<List<HeartRateSensorDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<HeartRateSensorDevice>> = _discoveredDevices.asStateFlow()
    
    private val _connectedDevice = MutableStateFlow<HeartRateSensorDevice?>(null)
    val connectedDevice: StateFlow<HeartRateSensorDevice?> = _connectedDevice.asStateFlow()

    /**
     * Check if Bluetooth is available and enabled
     */
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }
    
    /**
     * Start scanning for BLE heart rate sensors
     */
    @SuppressLint("MissingPermission")
    fun startScanning() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.w(TAG, "Bluetooth not available or not enabled")
            return
        }
        
        _sensorState.value = HeartRateSensorState.SCANNING
        _discoveredDevices.value = emptyList()
        
        val scanner = bluetoothAdapter.bluetoothLeScanner ?: return
        
        // Filter for devices advertising the Heart Rate Service
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(HeartRateServiceUuids.HEART_RATE_SERVICE))
            .build()
        
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        
        scanner.startScan(listOf(filter), settings, scanCallback)
        
        Log.d(TAG, "Started scanning for heart rate sensors")
    }
    
    /**
     * Stop scanning for devices
     */
    @SuppressLint("MissingPermission")
    fun stopScanning() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        if (_sensorState.value == HeartRateSensorState.SCANNING) {
            _sensorState.value = HeartRateSensorState.DISCONNECTED
        }
        Log.d(TAG, "Stopped scanning")
    }
    
    /**
     * Connect to a heart rate sensor device
     */
    @SuppressLint("MissingPermission")
    fun connectToDevice(device: HeartRateSensorDevice) {
        stopScanning()
        _sensorState.value = HeartRateSensorState.CONNECTING
        
        bluetoothGatt = device.device.connectGatt(context, false, gattCallback)
        Log.d(TAG, "Connecting to ${device.name}")
    }
    
    /**
     * Disconnect from current device
     */
    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.let { gatt ->
            gatt.disconnect()
            gatt.close()
        }
        bluetoothGatt = null
        _sensorState.value = HeartRateSensorState.DISCONNECTED
        _connectedDevice.value = null
        _currentHeartRate.value = null
        Log.d(TAG, "Disconnected")
    }
    
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val name = device.name ?: "Unknown Sensor"
            
            val sensorDevice = HeartRateSensorDevice(
                name = name,
                address = device.address,
                device = device
            )
            
            // Add to list if not already present
            val currentList = _discoveredDevices.value
            if (currentList.none { it.address == device.address }) {
                _discoveredDevices.value = currentList + sensorDevice
                Log.d(TAG, "Found device: $name (${device.address})")
            }
        }
        
        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "Scan failed with error: $errorCode")
            _sensorState.value = HeartRateSensorState.DISCONNECTED
        }
    }
    
    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected to GATT server")
                    _sensorState.value = HeartRateSensorState.CONNECTED
                    _connectedDevice.value = HeartRateSensorDevice(
                        name = gatt.device.name ?: "Heart Rate Sensor",
                        address = gatt.device.address,
                        device = gatt.device
                    )
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from GATT server")
                    _sensorState.value = HeartRateSensorState.DISCONNECTED
                    _connectedDevice.value = null
                    _currentHeartRate.value = null
                }
            }
        }
        
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Find the Heart Rate service
                val heartRateService = gatt.getService(HeartRateServiceUuids.HEART_RATE_SERVICE)
                if (heartRateService != null) {
                    // Get the Heart Rate Measurement characteristic
                    val hrCharacteristic = heartRateService.getCharacteristic(
                        HeartRateServiceUuids.HEART_RATE_MEASUREMENT
                    )
                    if (hrCharacteristic != null) {
                        // Enable notifications for heart rate measurements
                        gatt.setCharacteristicNotification(hrCharacteristic, true)
                        
                        // Write to the descriptor to enable notifications
                        val descriptor = hrCharacteristic.getDescriptor(
                            HeartRateServiceUuids.CLIENT_CONFIG_DESCRIPTOR
                        )
                        descriptor?.let {
                            it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt.writeDescriptor(it)
                        }
                        Log.d(TAG, "Enabled heart rate notifications")
                    }
                }
            }
        }
        
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == HeartRateServiceUuids.HEART_RATE_MEASUREMENT) {
                val heartRate = parseHeartRate(characteristic)
                _currentHeartRate.value = heartRate
                Log.d(TAG, "Heart rate: $heartRate bpm")
            }
        }
    }
    
    /**
     * Parse heart rate from the characteristic value.
     * The format is defined in the Bluetooth Heart Rate Service specification.
     */
    private fun parseHeartRate(characteristic: BluetoothGattCharacteristic): Int {
        val flag = characteristic.value[0].toInt()
        // Check if heart rate value is in UINT8 or UINT16 format
        return if (flag and 0x01 == 0) {
            // Heart Rate is in UINT8 format
            characteristic.value[1].toInt() and 0xFF
        } else {
            // Heart Rate is in UINT16 format
            (characteristic.value[1].toInt() and 0xFF) or 
                ((characteristic.value[2].toInt() and 0xFF) shl 8)
        }
    }
}
