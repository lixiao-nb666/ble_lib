//package com.newbee.ble_tool.util;
//
//
//
//
//public enum HudCmdType {
//    //直接发送指令
//    TIME((byte)0x01),//发送时间
//    SPEED((byte)0x02),//发送当前速度
//    SPEEDING((byte)0x1B),//超速
//    INTERVAL_SPEED((byte)0x03),//发送区间速度
//    WARNING_POINT((byte)0x04),//发送警示信息
//    BIG_WARNING_POINT((byte)0x0E),//发送大号警示信息
//    REACH_INFO((byte)0x05),//到达信息
//    LANE_INFORMATION((byte)0x06),//车道信息显示
//    TURN_TYPE((byte)0x07),//转向信息显示
//    Next_LANE_NAME((byte)0x08),//道路名称
//    NOW_LANE_STR((byte)0x14),//道路名称
//    GPS((byte)0x09),//GPS
//    SET_GPS_SPEED((byte)0x0D),//GPS
//    QUERY_GPS_SPEED((byte)0xFD),//查询GPS速度比例值
//    SET_BRIGHTNESS((byte)0x0A),//设置亮度
//    QUERY_BRIGHTNESS((byte)0xFA),//查询亮度
//    QUERY_SN((byte)0xFB),//查询亮度
//    REWRITE_SN((byte)0x0C),//重写SN码
//    QUERY_DEVICE_VERSION((byte)0xFC),//获得设备版本信息
//    SET_SOUND((byte)0xA1),//设置声音
//    QUERY_SOUND((byte)0xA2),//查询声音
//    READY_SEND_IMAGE((byte)0x10),//发送图片
//    SHOW_IMAGE((byte)0x11),//显隐实景图
//    YELLOW_STATU((byte)0x12),//显隐实景图
//    RETRUN_IMAGE((byte)0x55),
//    ICON_FLICKER((byte)0x13),//闪烁图标
//    FACTORY_SET((byte)0x15),//工厂设置
//    SET_DEVICE_SOUND_STATU((byte)0x16),//设置设备声音开关
//    QUERY_DEVICE_SOUND_STATU((byte)0x17),//查询设备声音开关
//    SET_DAYLIGHTING_SHOW_STATU((byte)0x18),//设置采光值开关
//    YELLOW_STATU_STR((byte)0x60),//设置黄色状态栏的值
//    WARNING_POINT_1_T_STR((byte)0x61),
//    WARNING_POINT_1_B_STR((byte)0x62),
//    WARNING_POINT_2_T_STR((byte)0x63),
//    WARNING_POINT_2_B_STR((byte)0x64),
//    REACH_STR((byte)0x65),
//    HIDE_INTERVAL_SPEED((byte)0x66),
//    CLEAR_PROGRESS_BAR((byte)0x67),
//    SEND_SPEED((byte)0x68),
//    SEND_WARNING_POINT_SPEED((byte)0x69),
////    SET_YELLOW_STATU_BJ((byte)0x70),
//    SET_TURN_BJ((byte)0x71),
////    SET_SPEEDING_BJ((byte)0x72),
//    SET_BLE_NAME((byte)0x73),//设置BLE数据蓝牙名字
//    SET_TWS_NAME((byte)0x74),//设置TWS蓝牙名字
//    SET_DISPLAY_RECT_SIZE((byte)0x75),//设置显示矩形的尺寸
//    SET_UI((byte)0x80),//设置UI风格
//
//    SET_BIG_TURN_TYPE_HIDE_AND_SHOW((byte)0x81),//大转向栏信息显示，隐藏
//    ;
//    private byte title;
//
//    HudCmdType(byte title){
//        this.title=title;
//    }
//
//
//    public byte getTitle() {
//        return title;
//    }
//}
