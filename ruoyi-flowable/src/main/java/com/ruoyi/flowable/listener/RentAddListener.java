package com.ruoyi.flowable.listener;

//import com.ruoyi.rent.domain.Rent;
import com.ruoyi.rent.mapper.RentMapper;
//import liquibase.pro.packaged.A;
//import org.flowable.common.engine.api.delegate.Expression;
import com.ruoyi.rent.domain.Rent;
import com.ruoyi.rent.service.RentService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.flowable.listener.config.SpringContextHolder;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RentAddListener implements ExecutionListener {
//    private Expression param;

//    模块A的@SpringBootApplication注解默认扫描范围为A的启动类所在的包（com.example.modulea）及其子包，所以此时模块A并没有扫描到模块B的stereotype，那么自然无法在模块A中注入模块B的Service类。
//    @Autowired
//    RentMapper rentMapper;

    @Override
    public void notify(DelegateExecution execution) {
        RentMapper rentMapper = SpringContextHolder.getBean(RentMapper.class);
        HistoryService historyService=SpringContextHolder.getBean(HistoryService.class);
        String id = execution.getProcessInstanceId();
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(id).list();
        System.out.println("11111"+list);

        Map<String, Object> historicVariablesMap = new HashMap<>();
        for (HistoricVariableInstance historicVariable : list) {
            String variableName = historicVariable.getVariableName();
            Object variableValue = historicVariable.getValue();
            historicVariablesMap.put(variableName, variableValue);
        }

        Rent rent = new Rent();

        Object auditTypeee = historicVariablesMap.get("auditTypeee");
        System.out.println("这里的o1"+auditTypeee);
        if (auditTypeee!=null&&auditTypeee!="") {
            if (auditTypeee.equals(1)) {
                rent.setTenant(historicVariablesMap.get("tenant") != null ? historicVariablesMap.get("tenant").toString() : null);
                rent.setId(historicVariablesMap.get("id")!= null ? historicVariablesMap.get("id").toString() : null);

                rentMapper.insert(rent);
            }
        }

    }
}
