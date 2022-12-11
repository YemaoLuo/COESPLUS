package com.coesplus.coes.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DashboardGradeVo {

    private String semester;

    private String avgGrade;

    public void setAvgGrade(double avgGrade) {
        this.avgGrade = String.format("%.2f", avgGrade);
    }
}
