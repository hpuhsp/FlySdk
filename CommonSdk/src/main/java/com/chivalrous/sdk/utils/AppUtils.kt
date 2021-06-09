package com.chivalrous.sdk.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import com.chivalrous.sdk.utils.encryption.EncryptUtils
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/2/8 10:23
 * @UpdateRemark:   更新说明：
 */
object AppUtils {
    private const val PREFS_FILE = "dev_id.xml"
    private const val DEVICE_UUID_FILE_NAME = ".dev_id.txt"
    private const val PREFS_DEVICE_ID = "dev_id"
    private const val KEY = "cyril'98"
    private const val SAVE_UDID_PATH = "perfect"

    @SuppressLint("ApplySharedPref")
    @Synchronized
    fun getMobileUDID(context: Context): String {
        var mDeviceId: String = ""
        val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_FILE, 0)
        val id = prefs.getString(PREFS_DEVICE_ID, "")
        if (!id.isNullOrEmpty()) {
            mDeviceId = id
        } else {
            val localUUID = recoverDeviceUuidFromSD(context)
            if (!localUUID.isNullOrEmpty()) {
                mDeviceId = localUUID
            } else {
                val mCombineStr: String
                val physicalUniqueID: String = getPhysicalUniqueID()
                mCombineStr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val androidId: String = getAndroidID(context)
                    try {
                        if ("9774d56d682e549c" != androidId) {
                            physicalUniqueID + androidId
                        } else {
                            physicalUniqueID + UUID.randomUUID()
                        }
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                } else {
                    physicalUniqueID + getWLANMACAddress(context) + getBTMACAddress()
                }
                // Compute Md5 Action
                try {
                    val messageDigest = MessageDigest.getInstance("MD5")
                    messageDigest.update(mCombineStr.toByteArray(), 0, mCombineStr.length)
                    val md5Bytes = messageDigest.digest()
                    // Get Md5 Bytes
                    // Create A Hex String
                    var hexStr: String = ""
                    for (i in md5Bytes.indices) {
                        val b = 0xFF and md5Bytes[i].toInt()
                        // if it is a single digit, make sure it have 0 in front (proper padding)
                        if (b <= 0xF) hexStr += "0"
                        // add number to string
                        hexStr += Integer.toHexString(b)
                    }
                    mDeviceId = hexStr.toUpperCase(Locale.getDefault())
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }

                // 保存至本地
                try {
                    saveDeviceUuidToSD(context, EncryptUtils.encryptDES(mDeviceId, KEY))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            prefs.edit().putString(PREFS_DEVICE_ID, mDeviceId)
                .commit()
        }
        return mDeviceId
    }

    /**
     * 从SDK里面取
     *
     * @return
     */
    private fun recoverDeviceUuidFromSD(context: Context): String? {
        return try {
            val dirPath: String = FileUtils.getSavePath(context, SAVE_UDID_PATH)
            val dir = File(dirPath)
            val uuidFile = File(dir, DEVICE_UUID_FILE_NAME)
            if (!dir.exists() || !uuidFile.exists()) {
                return null
            }
            val fileReader = FileReader(uuidFile)
            val sb = StringBuilder()
            val buffer = CharArray(100)
            var readCount: Int
            while (fileReader.read(buffer).also { readCount = it } > 0) {
                sb.append(buffer, 0, readCount)
            }
            //通过UUID.fromString来检查uuid的格式正确性
            val uuid = UUID.fromString(
                EncryptUtils.decryptDES(
                    sb.toString(),
                    KEY
                )
            )
            uuid.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * The Android ID
     * 通常被认为不可信，因为它有时为null。开发文档中说明了：这个ID会改变如果进行了出厂设置。并且，如果某个
     * Andorid手机被Root过的话，这个ID也可以被任意改变。无需任何许可。
     *
     * @return AndroidID
     */
    @SuppressLint("HardwareIds")
    fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: ""
    }

    /**
     * Pseudo-Unique ID, 这个在任何Android手机中都有效
     * 有一些特殊的情况，一些如平板电脑的设置没有通话功能，或者你不愿加入READ_PHONE_STATE许可。而你仍然想获得唯
     * 一序列号之类的东西。这时你可以通过取出ROM版本、制造商、CPU型号、以及其他硬件信息来实现这一点。这样计算出
     * 来的ID不是唯一的（因为如果两个手机应用了同样的硬件以及Rom 镜像）。但应当明白的是，出现类似情况的可能性基
     * 本可以忽略。大多数的Build成员都是字符串形式的，我们只取他们的长度信息。我们取到13个数字，并在前面加上“35
     * ”。这样这个ID看起来就和15位IMEI一样了。
     *
     * @return getDeviceUniqueID
     */
    private fun getPhysicalUniqueID(): String {
        var m_szDevIDShort = "35" + //we make this look like a valid IMEI
                // 主板
                Build.BOARD.length % 10 // android系统定制商
        +(Build.BRAND.length % 10 // 设备参数
                ) + Build.DEVICE.length % 10 // 显示屏参数
        +(Build.DISPLAY.length % 10
                ) + Build.HOST.length % 10 // 修订版本列表
        +(Build.ID.length % 10 // 硬件制造商
                ) + Build.MANUFACTURER.length % 10 // 版本
        +(Build.MODEL.length % 10
                ) + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 // builder的类型
        +(Build.TYPE.length % 10
                ) + Build.SERIAL.length % 10 + Build.USER.length % 10 //13 digits

        // CPU指令集
        m_szDevIDShort += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS.size % 10
        } else {
            Build.CPU_ABI.length % 10
        }
        return m_szDevIDShort
    }

    /**
     * The WLAN MAC Address string
     * 是另一个唯一ID。但是你需要为你的工程加入android.permission.ACCESS_WIFI_STATE 权限，否则这个地址会为
     * null。Returns: 00:11:22:33:44:55 (这不是一个真实的地址。而且这个地址能轻易地被伪造。).WLan不必打开，
     * 就可读取些值。
     *
     * @return m_szWLANMAC
     */
    @SuppressLint("WifiManagerPotentialLeak", "HardwareIds")
    private fun getWLANMACAddress(context: Context): String {
        val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wm.connectionInfo.macAddress ?: ""
    }

    /**
     * 只在有蓝牙的设备上运行。并且要加入android.permission.BLUETOOTH 权限.Returns: 43:25:78:50:93:38 .
     * 蓝牙没有必要打开，也能读取。
     *
     * @return m_szBTMAC
     */
    private fun getBTMACAddress(): String {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return if (null != bluetoothAdapter) {
            bluetoothAdapter.address ?: ""
        } else {
            ""
        }
    }

    /**
     * 存储到SDK
     *
     * @param uuid
     */
    private fun saveDeviceUuidToSD(context: Context, uuid: String) {
        val dirPath: String = FileUtils.getSavePath(context, SAVE_UDID_PATH)
        val targetFile = File(dirPath, DEVICE_UUID_FILE_NAME)
        if (null != targetFile && targetFile.exists()) {
            val osw: OutputStreamWriter
            try {
                osw = OutputStreamWriter(FileOutputStream(targetFile), "utf-8")
                try {
                    osw.write(uuid)
                    osw.flush()
                    osw.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

}