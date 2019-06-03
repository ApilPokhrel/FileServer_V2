package com.fileserver.app.works.user.entity;

import java.util.ArrayList;

public class ExtrasModel {
    private String plan;
    private ArrayList<String> plan_dates;

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public ArrayList<String> getPlan_dates() {
        return plan_dates;
    }

    public void setPlan_dates(ArrayList<String> plan_dates) {
        this.plan_dates = plan_dates;
    }
}
