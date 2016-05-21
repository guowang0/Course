package com.jiusg.course.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/11.
 */
public class Schedule implements Serializable {

    private int week = -1; // 第几周的课表
    private String classInfo; // 课程信息
    private String classTeacher; // 教师
    private int classTime; // 时间
    private String classPlace; //地点
    private String classNam; // 课程名称;
    private int classDate; // 周几的课


    public int getClassDate() {
        return classDate;
    }

    public void setClassDate(int classDate) {
        this.classDate = classDate;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(String classInfo) {
        this.classInfo = classInfo;
    }

    public String getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(String classTeacher) {
        this.classTeacher = classTeacher;
    }

    public int getClassTime() {

        return classTime;
    }


    public void setClassTime(int classTime) {
        this.classTime = classTime;
    }

    public String getClassPlace() {
        return classPlace;
    }

    public void setClassPlace(String classPlace) {
        this.classPlace = classPlace;
    }

    public String getClassNam() {
        return classNam;
    }

    public void setClassNam(String classNam) {
        this.classNam = classNam;
    }
}
