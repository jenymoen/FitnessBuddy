package com.fitnessbuddy.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import java.util.UUID

@SuppressLint("MissingPermission")
class TreadmillManager(private val bluetoothController: BluetoothController) {

    // FTMS Control Point Characteristic (UUID 0x2AD9)
    private val FTMS_CONTROL_POINT_UUID = UUID.fromString("00002AD9-0000-1000-8000-00805f9b34fb")
    
    // Opcodes per FTMS spec
    private val OPCODE_REQUEST_CONTROL = 0x00.toByte()
    private val OPCODE_RESET = 0x01.toByte()
    private val OPCODE_SET_TARGET_SPEED = 0x02.toByte()
    private val OPCODE_SET_TARGET_INCLINATION = 0x03.toByte()

    fun requestControl() {
        sendCommand(byteArrayOf(OPCODE_REQUEST_CONTROL))
    }

    fun setSpeed(kmh: Float) {
        // Speed is typically UINT16 with resolution 0.01 km/h
        val value = (kmh * 100).toInt()
        val payload = byteArrayOf(
            OPCODE_SET_TARGET_SPEED,
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte()
        )
        sendCommand(payload)
    }
    
    fun setIncline(percent: Float) {
        // Incline is typically SINT16 with resolution 0.1%
        val value = (percent * 10).toInt()
         val payload = byteArrayOf(
            OPCODE_SET_TARGET_INCLINATION,
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte()
        )
        sendCommand(payload)
    }

    private fun sendCommand(data: ByteArray) {
        val gatt = bluetoothController.getGatt() ?: return
        val service = gatt.services.find { it.uuid.toString().startsWith("00001826") } 
        val characteristic = service?.getCharacteristic(FTMS_CONTROL_POINT_UUID)
        
        if (characteristic != null) {
            characteristic.value = data
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            gatt.writeCharacteristic(characteristic)
        }
    }
}
