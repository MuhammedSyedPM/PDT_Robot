package com.zebra.demo.rfidreader.data_export;


import android.util.Log;

import com.zebra.demo.BuildConfig;

import org.apache.commons.lang3.ArrayUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

public class PasswordEncryptor {

    private final byte[] AppId = { 0x41, (byte) 0xFA, (byte) 0xEE, (byte) 0xD1, 0x18, (byte) 0xD3, 0x43, 0x43, (byte) 0x8B,
            0x5A, 0x21, (byte) 0xCC, 0x65, 0x7D, (byte) 0xD7, 0x10 };
    private final int[] tbl = { 0x4987, 0x8542, 0x9D96, 0xCD04, 0xEEA4, 0x38EE, 0xF84A, 0xA38C, 0xB18A, 0xF4DF,
            0xB592, 0xF54B, 0xC622, 0x0EEE, 0xC9DA, 0xC04B, 0x62D3, 0x1ED9, 0x1C4B, 0xA5BD,
            0x1BC9, 0x79D5, 0x3C55, 0xBEA7, 0x68EE, 0x0D82, 0x7FFB, 0xC3B6, 0x913A, 0x7C7B,
            0x87DF, 0x3B13, 0xE4ED, 0x8C24, 0x2CCA, 0x6F65, 0xE436, 0xC4EC, 0x1820, 0x0E70,
            0x6466, 0x86DB, 0xCC3D, 0xCB66, 0x1067, 0x47E1, 0xCF29, 0x353B, 0x8E65, 0xF14F,
            0xC899, 0x7B9D, 0x7EA7, 0x9D49, 0x37D6, 0x9D71, 0x2B0B, 0x1621, 0x585F, 0x47A6,
            0x2125, 0x14C4, 0x838E, 0x96BB, 0x0088, 0x5EB3, 0x2EBF, 0xA7CB, 0xEBE8, 0x5C24,
            0x9F82, 0x2F39, 0xCAD5, 0xB6C3, 0x1314, 0xE32E, 0x1E4A, 0x2DA2, 0x84B7, 0x1AF0,
            0x10E2, 0x48AE, 0x78EA, 0x436B, 0x634B, 0x3570, 0x0322, 0x079E, 0xF6DB, 0xFAAD,
            0xD7D3, 0xA7EC, 0xA530, 0x94E4, 0x9204, 0x4634, 0xCD6C, 0x13BC, 0x3A80, 0xC1DF,
            0x924A, 0x5FC8, 0xDE73, 0x2BFB, 0x438E, 0xACA9, 0xBC6C, 0x183D, 0x3B5B, 0x0948,
            0x565E, 0xD95E, 0x0401, 0xA296, 0x0E4A, 0x6476, 0x1A1A, 0x1B8D, 0x73DF, 0x0D32,
            0x0498, 0xA103, 0x589D, 0x7760, 0x54D6, 0x81DE, 0x7678, 0x8933, 0x7001, 0xC4DD,
            0xD094, 0xECF8, 0x2A36, 0x8FA1, 0x518A, 0xCA5B, 0x4ED7, 0x43BD, 0xBDD5, 0x0666,
            0x8941, 0x4138, 0xC986, 0xFFA1, 0x91E7, 0x1D72, 0x1B30, 0x6170, 0xACA8, 0xD922,
            0x0ED5, 0x0FC9, 0x0B25, 0x909F, 0x35CC, 0x769A, 0xFB5D, 0x538F, 0x52B4, 0x2EA3,
            0x36EE, 0x0F69, 0x6D58, 0x401D, 0x76D3, 0x1114, 0xFCE3, 0x58B3, 0x3178, 0x2CA4,
            0xF560, 0x6BC5, 0x7182, 0x76BD, 0xDDD1, 0x1DF8, 0x7935, 0xEB20, 0xC5A5, 0x5CB3,
            0xE472, 0x2861, 0xBB61, 0x9E17, 0x059F, 0x5DDA, 0x878A, 0xAED6, 0x824A, 0xF8EB,
            0xE182, 0x40A4, 0xB618, 0x9AC6, 0xF837, 0xEE8C, 0x1B79, 0xBF83, 0x370A, 0x0A10,
            0xE35F, 0xB83D, 0x7342, 0x70EE, 0x8715, 0x9E7B, 0xAD71, 0xD261, 0x3283, 0xABA2,
            0xC6D7, 0x1FCD, 0xB1D1, 0xBF0E, 0xCB2A, 0x9E77, 0x40E7, 0x0781, 0xCA02, 0x3174,
            0xA7AC, 0x3EE3, 0x9972, 0xDEAA, 0xA65D, 0x51B4, 0x5E68, 0x490B, 0x40EA, 0x8CA1,
            0x8262, 0x235C, 0x7626, 0x3BC5, 0x6A51, 0x962F, 0x5237, 0xFA1A, 0x993B, 0x343E,
            0xF214, 0x676E, 0xD9EA, 0x19D7, 0x9EFB, 0x41E0, 0x758F, 0xF145, 0x88E1, 0x6C73,
            0x3719, 0x4055, 0xC621, 0x6227, 0x6D2F, 0x7204
    };

    public String getEncryptedPassword(String password) {
        String encodedPassword = getEncodedPassword(password);
        return constructEncryptedPassword(encodedPassword);
    }

    private String getEncodedPassword(String initialPassword) {
        String encr1 = encryptPasswordWithSHA512Hashing(initialPassword);
        String encr2 = new String(Base64.getEncoder().encode(AppId),StandardCharsets.UTF_8);
        String compressCode = "123;sdf#234asSDFSDCVwer";
        String encr3 = encryptPasswordWithSHA512Hashing(compressCode);
        return encr1 + encr2 + encr3;
    }

    private String constructEncryptedPassword(String encodedPwd) {
        String d1 = encryptPasswordWithSHA512Hashing(encodedPwd);
        String d1Reverse = new StringBuilder(encodedPwd).reverse().toString();
        String d2 = encryptPasswordWithSHA512Hashing(d1Reverse);
        byte[] a = d1.getBytes(StandardCharsets.UTF_8);
        byte[] b = d2.getBytes(StandardCharsets.UTF_8);
        ArrayList<Byte> c = new ArrayList<>();
        for(byte i: a) {
            c.add(i);
        }
        for(byte j: b) {
            c.add(j);
        }
        ArrayList<Byte> buff = new ArrayList<>();
        int j = 0;
        for (int i=0;i<256;i++)
        {
            byte b1 = (byte)(tbl[i] % 256);
            byte b2 = (byte)(tbl[i] / 256);

            byte b3 = (byte)(b1 & b2);
            byte b4 = (byte)(b1 | b2);
            byte b5 = (byte)(b1 ^ b2);

            if(j >= c.size()) {
                break;
            }
            byte b6 = (byte)(c.get(j) & b1);
            byte b7 = (byte)(c.get(j) & b2);

            byte b8 = (byte)(c.get(j) | b7);
            byte b9 = (byte)(c.get(j) ^ b8);
            byte b10 = (byte)( b9 << 2 );

            buff.add(b1);buff.add(b2);
            buff.add(b3);buff.add(b4);buff.add(b5);
            buff.add(b6);buff.add(b7);buff.add(b8);
            buff.add(b9);buff.add(b10);
            j++;
        }
        Byte[] buffArray = new Byte[buff.size()];
        buff.toArray(buffArray);
        String b64Buff= new String(Base64.getEncoder().encode(ArrayUtils.toPrimitive(buffArray)),StandardCharsets.UTF_8);
        String d3 = d2.substring(0, 32);
        String d4 = encryptPasswordWithSHA512Hashing(b64Buff).substring(0,32);
        StringBuilder builder = new StringBuilder();
        builder.append(d1).append(d3).append(d4);
        builder.reverse();
        return builder.toString();
    }

    private String encryptPasswordWithSHA512Hashing(String userPwd) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(userPwd.getBytes(StandardCharsets.UTF_8));
            byte[] response = digest.digest();
            BigInteger no = new BigInteger(1, response);
            String hashtext = no.toString(16);
            while (hashtext.length() < 128) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }catch (NoSuchAlgorithmException e) {
            if(BuildConfig.DEBUG)
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String stackTraceString = sw.toString();
                Log.e("PasswordEncryptor", stackTraceString);
            }        }
        return null;
    }
}
