// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.execution.ui;

import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static com.intellij.util.containers.ContainerUtil.exists;

public class CommonJavaFragments {

  public static <S extends CommonProgramRunConfigurationParameters> SettingsEditorFragment<S, ?> createEnvParameters() {
    EnvironmentVariablesComponent env = new EnvironmentVariablesComponent();
    env.setLabelLocation(BorderLayout.WEST);
    return SettingsEditorFragment.create("environmentVariables",
                                         ExecutionBundle.message("environment.variables.fragment.name"),
                                         ExecutionBundle.message("group.java.options"), env);
  }

  public static <S extends RunConfigurationBase<?>> SettingsEditorFragment<S, JLabel> createBuildBeforeRun() {
    String buildAndRun = ExecutionBundle.message("application.configuration.title.build.and.run");
    String run = ExecutionBundle.message("application.configuration.title.run");
    JLabel jLabel = new JLabel(buildAndRun);
    jLabel.setFont(JBUI.Fonts.label().deriveFont(Font.BOLD));
    return new SettingsEditorFragment<S, JLabel>("buildBeforeRun",
                                                 ExecutionBundle.message("build.before.run"),
                                                 ExecutionBundle.message("group.java.options"),
                                                 jLabel, -1,
                                                 (s, label) -> {
                                                   label.setText(exists(s.getBeforeRunTasks(),
                                                                        t -> CompileStepBeforeRun.ID == t.getProviderId())
                                                                 ? buildAndRun
                                                                 : run);
                                                 },
                                                 (s, label) -> {
                                                   if (buildAndRun.equals(label.getText())) {
                                                     if (!exists(s.getBeforeRunTasks(),
                                                                 t -> CompileStepBeforeRun.ID == t.getProviderId())) {
                                                       CompileStepBeforeRun.MakeBeforeRunTask task =
                                                         new CompileStepBeforeRun.MakeBeforeRunTask();
                                                       task.setEnabled(true);
                                                       ArrayList<BeforeRunTask<?>> tasks = new ArrayList<>(s.getBeforeRunTasks());
                                                       tasks.add(task);
                                                       s.setBeforeRunTasks(tasks);
                                                     }
                                                   }
                                                   else {
                                                     ArrayList<BeforeRunTask<?>> tasks = new ArrayList<>(s.getBeforeRunTasks());
                                                     tasks.removeIf(t -> CompileStepBeforeRun.ID == t.getProviderId());
                                                     s.setBeforeRunTasks(tasks);
                                                   }
                                                 },
                                                 s -> true) {
      @Override
      public void setSelected(boolean selected) {
        jLabel.setText(selected ? buildAndRun : run);
        fireEditorStateChanged();
      }

      @Override
      public boolean isSelected() {
        return buildAndRun.equals(jLabel.getText());
      }
    };
  }
}
