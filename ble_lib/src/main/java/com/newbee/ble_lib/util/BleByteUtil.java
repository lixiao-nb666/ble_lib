package com.newbee.ble_lib.util;



public class BleByteUtil {


    public static byte[] intToByteArray32(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] intToByteArray32only3(int a) {
        return new byte[] {
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] intToByteArray32only2(int a) {
        return new byte[] {
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] intToByteArray32only1(int a) {
        return new byte[] {
                (byte) (a & 0xFF)
        };
    }

    /**

     * byte[]转int

     * @param bytes

     * @return

     */

    public static int byteArrayToInt(byte[] bytes) {
        int value= 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift= (4 - 1 - i) * 8;
            value +=(bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }


    public static String parseByte2HexStr(byte buf) {

            String hex = Integer.toHexString(buf& 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

        return hex.toUpperCase();
    }


//    private static int defMixLength=5;
//    public static byte[] useTitleAndBodyGetAllCmdBytes(byte title,byte[] body){
//        int needLength=defMixLength;
//        if(null!=body&&body.length>0){
//            needLength+=body.length;
//        }
//        byte[] result = new byte[needLength];
//        result[0]= T800Config.cmdt1;
//        result[1]= T800Config.cmdt2;
//        result[2]= (byte) ((needLength >> 8) & 0xFF);
//        result[3]= (byte) (needLength & 0xFF);
//        result[4]=title;
//        if(needLength>defMixLength){
//            for(int i=0;i<body.length;i++){
//                result[i+5] = body[i];
//            }
//        }
//        if(defMixLength>=7){
//            result[needLength-2]=T800Config.end1;
//            result[needLength-1]=T800Config.end2;
//        }
//        return result;
//    }

   public static byte[] resultData(byte[] data){
        int length = data.length + 4;
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            if (i == 0){
                result[i] = (byte) 0xA5;
            }else if (i == 1){
                result[i] = (byte) 0x5A;
            }else if (i == 2){
                result[i] = (byte) ((length >> 8) & 0xFF);
            }else if (i == 3){
                result[i] = (byte) (length & 0xFF);
            }else{
                result[i] = data[i - 4];
            }
        }
        return result;
    }


    public static byte[] addToNewBytes(byte[]...bs){
        int needL=0;
        for(byte[]bytes:bs){
            needL+=bytes.length;
        }

        byte[] newBytes=new byte[needL];
        int index=0;
        for(byte[]bytes:bs){
            for(byte b:bytes){
                newBytes[index]=b;
                index++;
            }
        }
        return newBytes;
    }


    public static String getCmdStrK(byte[] cmd){
        String kStr;
        if(cmd.length<5){
            kStr=BleByteUtil.parseByte2HexStr(cmd);
        }else {
            byte[]kBytes=new byte[5];
            System.arraycopy(cmd, 0, kBytes, 0, 5);
            kStr= BleByteUtil.parseByte2HexStr(kBytes);
        }
        return  kStr;
    }






}
