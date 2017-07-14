/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.samples.portlet.actioncommand.test;

import com.liferay.arquillian.portal.annotation.PortalURL;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.File;

import java.net.URL;

import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Lawrence Lee
 */
@RunAsClient
@RunWith(Arquillian.class)
public class BladePortletActionCommandTest {

	@Deployment
	public static JavaArchive create() throws Exception {
		final File jarFile = new File(
			System.getProperty("portletActionommandJarFile"));

		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
	}

	public void customClick(WebDriver webDriver, WebElement webElement) {
		Actions action = new Actions(webDriver);

		action.moveToElement(webElement).build().perform();

		WebDriverWait wait = new WebDriverWait(webDriver, 5);

		WebElement element = wait.until(
			ExpectedConditions.visibilityOf(webElement));

		element.click();
	}

	@Test
	public void testBladePortletActionCommand()
		throws InterruptedException, PortalException {

		_webDriver.get(_portletURL.toExternalForm());

		Assert.assertTrue(
			"Portlet was not deployed",
			isVisible(_bladeSampleActionCommandGreeterPortlet));

		Assert.assertTrue("Name Field is not visible", isVisible(_nameField));

		_webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

		_nameField.clear();

		_nameField.sendKeys("tester");

		customClick(_webDriver, _saveButton);

		Assert.assertTrue(
			"Expected Hello tester! Welcome to OSGi Hello from BLADE!, but saw " +
				_portletBody.getText(),
			isTextPresent(
				_portletBody,
				"Hello tester! Welcome to OSGi Hello from BLADE!"));
	}

	protected boolean isTextPresent(WebElement webelement, String string) {
		WebDriverWait webDriverWait = new WebDriverWait(_webDriver, 5);

		try {
			webDriverWait.until(
				ExpectedConditions.textToBePresentInElement(
					webelement, string));

			return true;
		}
		catch (org.openqa.selenium.TimeoutException te) {
			return false;
		}
	}

	protected boolean isVisible(WebElement webelement) {
		WebDriverWait webDriverWait = new WebDriverWait(_webDriver, 5);

		try {
			webDriverWait.until(ExpectedConditions.visibilityOf(webelement));

			return true;
		}
		catch (org.openqa.selenium.TimeoutException te) {
			return false;
		}
	}

	@FindBy(xpath = "//div[contains(@id,'com_liferay_blade_samples_portlet_actioncommand_GreeterPortlet')]")
	private WebElement _bladeSampleActionCommandGreeterPortlet;

	@FindBy(xpath = "//input[@type='text' and contains(@id,'com_liferay_blade_samples_portlet_actioncommand_GreeterPortlet')]")
	private WebElement _nameField;

	@FindBy(xpath = "//div[contains(@id,'com_liferay_blade_samples_portlet_actioncommand_GreeterPortlet')]//..//div/div")
	private WebElement _portletBody;

	@PortalURL("com_liferay_blade_samples_portlet_actioncommand_GreeterPortlet")
	private URL _portletURL;

	@FindBy(xpath = "//button[@type='submit' and contains(.,'Save')]")
	private WebElement _saveButton;

	@Drone
	private WebDriver _webDriver;

}