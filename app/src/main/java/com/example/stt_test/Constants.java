package com.example.stt_test;

import java.nio.file.attribute.PosixFileAttributes;

public class Constants {
    public static final int video_idle = 0;
    public static final int video_what = 1;
    public static final int video_error = 2;
    public static final int video_net_error = 3;
    public static final int video_introduce = 4;
    public static final int video_standby_mode = 5;
    public static final int video_miss = 6;

    public static final int video_time = 7;
    public static final int video_date = 8;
    public static final int video_alarm_cancle = 9;
    public static final int video_alarm = 10; //알람 성공이랑 소리가 같으니 충돌관련 조심!!
    public static final int video_alarm_error = 11;
    public static final int video_alarm_alart = 12; // 알람 시간되면 울림
    public static final int video_alarm_empty = 13;

    public static final int video_timer_cancle = 14;
    public static final int video_timer = 15; //알람 성공이랑 소리가 같으니 충돌관련 조심!!
    public static final int video_timer_error = 16;
    public static final int video_timer_alart = 17; // 알람 시간되면 울림
    public static final int video_timer_empty = 18;
    public static final int video_tomorrow_weather = 19;
    public static final int video_today_weather = 20;





    // 여기서부턴 음성명령어 성공부분
    public static final int video_success=21;

    public static final int lucia_call_on = 1;
    public static final int lucia_call_off = 0;

    public static final int lucia_voice_10sec_reset = 0;

    public static final int audio_what = 1;
    public static final int audio_error = 2;
    public static final int audio_net_error = 3;
    public static final int audio_introduce = 4;
    public static final int audio_standby_mode = 5;

    //여기서부턴 음성명령어 성공부분
    public static final int audio_success_bedroom=6;



    //여기서부턴 시간, 날짜, 알람, 타이머 부분
    public static final int lucia_time_start = 14;
    public static final int lucia_time_hour = 11;
    public static final int lucia_time_minute = 12;
    public static final int lucia_time_sec = 13;
    public static final int lucia_time_end = 16;


    public static final int lucia_alarm_start = 19;
    public static final int lucia_alarm_end = 20;
    public static final int lucia_alarm_error = 21;
    public static final int lucia_timer_end = 21;

    public static final int lucia_day_start = 15;
    public static final int lucia_day_month = 17;
    public static final int lucia_day_date = 18;
    public static final int lucia_day_end = 0;

    // 타이머, 알람 동작시 락온오프 변수가 필요한게 맞다.
    public static final int lucia_alarm_timer_lock_off = 0;
    public static final int lucia_alarm_timer_lock_alart = 2;


    public static final int lucia_alarm_timer_empty_error = 999;

    public static final int lucia_alarm_timer_mute = 0;


    // 날씨용 constants
    public static final int today_weather = 11;
    public static final int tomorrow_weather = 12;
    public static final int temp_hue_is = 13;
    public static final int temp_hue = 14;
    public static final int temp_hue_c = 15;
    public static final int temp_hue_per = 16;
    public static final int alarm_end = 17;
    public static final int temp_hue_max = 18;
    public static final int temp_hue_min = 19;

    public static final int weather_value_null = 999;

    public static final int weather_list = 20; //날씨 리스트

    public static final int weather_mist_start = 27; // 미스트부터 동일 리스트인데 분류되어있는거 통합용
    public static final int weather_mist_end = 35;

    public static final int lucia_10_v = 10;
    public static final int lucia_subzero_v = 28;
    public static final int lucia_mute_v = 29;
    public static final int weather_font_1 = 1;



    public static final int video_standby_sleep = 99;

    public static final int wake_up = 98;





}
