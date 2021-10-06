/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camunda.example.service;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Slf4j
@Component("logger")
public class LoggerDelegate implements JavaDelegate {

  private String injectedProperty;

  public String getInjectedProperty() {
    return injectedProperty;
  }

  public void setInjectedProperty(String injectedProperty) {
    this.injectedProperty = injectedProperty;
  }

  public void execute(DelegateExecution exec) {


    ExtensionElements extensionElements = exec.getBpmnModelElementInstance().getExtensionElements();
    if (extensionElements != null) {
      try {
        CamundaProperties camProps = extensionElements
            .getElementsQuery().filterByType(CamundaProperties.class).singleResult();
        if (camProps != null) {
          for (CamundaProperty prop : camProps.getCamundaProperties())
            log.info("Camunda property {} with value {}", prop.getCamundaId(), prop.getCamundaValue());
        }
      }catch (BpmnModelException e) {
        log.debug("No extension property set");
      }
    }

    log.info("Injected property: {}", injectedProperty);

    log.info("\n\n LoggerDelegate invoked by processDefinitionId: {}, activityId: {}, activityName: '{}'," +
            " processInstanceId: {}, businessKey: {}, executionId: {}, modelName: {}, elementId: {} \n",
        exec.getProcessDefinitionId(),
        exec.getCurrentActivityId(),
        exec.getCurrentActivityName().replaceAll("\n", " "),
        exec.getProcessInstanceId(),
        exec.getProcessBusinessKey(),
        exec.getId(),
        exec.getBpmnModelInstance().getModel().getModelName(),
        exec.getBpmnModelElementInstance().getId()
    );

    log.info("--- Variables ---");
    Map<String, Object> variables = exec.getVariables();
    for (Map.Entry<String, Object> entry : variables.entrySet())
      log.info(entry.getKey() + " : " + entry.getValue());
  }

  public void executeWith(DelegateExecution exec, String param) {
    log.info("Parameter from executeWith method: {}", param);

    log.info("Injected property: {}", injectedProperty);

    execute(exec);
  }

}