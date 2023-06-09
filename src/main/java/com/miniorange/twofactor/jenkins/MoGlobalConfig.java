/*
 * Copyright (c) 2023
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.miniorange.twofactor.jenkins;

import static com.miniorange.twofactor.constants.MoGlobalConfigConstant.AdminConfiguration.ENABLE_2FA;
import static com.miniorange.twofactor.jenkins.MoFilter.moPluginSettings;

import com.miniorange.twofactor.jenkins.DTO.MoOtpOverEmailDto;
import hudson.Extension;
import hudson.model.Descriptor;
import java.util.logging.Logger;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public class MoGlobalConfig extends GlobalConfiguration {

  private static final Logger LOGGER = Logger.getLogger(MoGlobalConfig.class.getName());
  private Boolean enableTfa;
  private Boolean enableSecurityQuestionsAuthentication;
  private MoOtpOverEmailDto otpOverEmailDto;

  public MoGlobalConfig() {
    load();
  }

  public Boolean getEnableTfa() {
    return enableTfa != null ? enableTfa : false;
  }

  @SuppressWarnings("unused")
  @DataBoundSetter
  public void setEnableTfa(boolean unableTfa) {
    enableTfa = unableTfa;
  }

  @SuppressWarnings("unused")
  public Boolean isEnableSecurityQuestionsAuthentication() {
    return enableSecurityQuestionsAuthentication != null
        ? enableSecurityQuestionsAuthentication
        : false;
  }

  @SuppressWarnings("unused")
  @DataBoundSetter
  public void setEnableSecurityQuestionsAuthentication(
      boolean enableSecurityQuestionsAuthentication) {
    this.enableSecurityQuestionsAuthentication = enableSecurityQuestionsAuthentication;
  }

  @SuppressWarnings("unused")
  public Boolean isEnableOtpOverEmailAuthentication() {
    return otpOverEmailDto != null ? otpOverEmailDto.getIsEnabled() : false;
  }

  @SuppressWarnings("unused")
  public String getSenderEmailAddress() {
    return otpOverEmailDto != null ? otpOverEmailDto.getSenderEmailAddress() : "";
  }

  public MoOtpOverEmailDto getOtpOverEmailDto() {
    return otpOverEmailDto;
  }

  public void saveMoGlobalConfigViewForm(JSONObject formData) {
    try {
      enableTfa = formData.getBoolean("enableTfa");
      moPluginSettings.put(ENABLE_2FA.getKey(), enableTfa);
      enableSecurityQuestionsAuthentication = formData.getBoolean("enableSecurityQuestion");

      if (formData.containsKey("enableOtpOverEmail")) {
        JSONObject otpOverEmail = formData.getJSONObject("enableOtpOverEmail");
        otpOverEmailDto = new MoOtpOverEmailDto(true, otpOverEmail.getString("senderEmailAddress"));
      } else {
        otpOverEmailDto = null;
      }

      this.save();
      LOGGER.fine("Saving global configuration ");
    } catch (Exception e) {
      LOGGER.fine("Error in saving mo global configuration " + e.getMessage());
    }
  }

  @Override
  public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {

    if (formData.has("enableTfa")) {
      saveMoGlobalConfigViewForm(formData);
    }

    return super.configure(req, formData);
  }

  public static MoGlobalConfig get() {
    final MoGlobalConfig config;
    try {
      config = GlobalConfiguration.all().get(MoGlobalConfig.class);
    } catch (IllegalStateException e) {
      LOGGER.fine("Error in fetching global configuration class " + e.getMessage());
      throw e;
    }
    return config;
  }

  public static final MoGlobalConfig.DescriptorImpl DESCRIPTOR =
      new MoGlobalConfig.DescriptorImpl();

  @SuppressWarnings("unused")
  @Extension
  public static final class DescriptorImpl extends Descriptor<GlobalConfiguration> {

    public DescriptorImpl() {
      super();
    }

    public DescriptorImpl(Class<? extends GlobalConfiguration> clazz) {
      super(clazz);
    }
  }
}
