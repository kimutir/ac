///*
// * Copyright 2017-2023 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.amvera.cli.custom;
//
//import org.jline.utils.AttributedString;
//import org.springframework.core.io.Resource;
//import org.springframework.shell.Utils;
//import org.springframework.shell.command.CommandRegistration;
//import org.springframework.shell.standard.*;
//import org.springframework.shell.standard.commands.Help;
//import org.springframework.shell.style.TemplateExecutor;
//import org.springframework.util.FileCopyUtils;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.io.UncheckedIOException;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * A command to display help about all available commands.
// *
// * @author Eric Bottard
// * @author Janne Valkealahti
// */
//@ShellComponent
//public class HelpTest extends AbstractShellComponent implements Help.Command {
//
//	private boolean showGroups = true;
//	private TemplateExecutor templateExecutor;
//	private String commandTemplate;
//	private String commandsTemplate;
//
//
//	public HelpTest(TemplateExecutor templateExecutor) {
//		this.templateExecutor = templateExecutor;
//	}
//
//	@ShellMethod(value = "CUSTOM Display help about available commands")
//	public AttributedString help(
//			@ShellOption(defaultValue = ShellOption.NULL, valueProvider = CommandValueProvider.class, value = { "-c",
//					"--command" }, help = "The command to obtain help for.", arity = Integer.MAX_VALUE) String[] command)
//			throws IOException {
//		if (command == null) {
//			return renderCommands();
//		}
//		else {
//			String commandStr = Stream.of(command)
//				.map(c -> c.trim())
//				.collect(Collectors.joining(" "));
//			return renderCommand(commandStr);
//		}
//	}
//
//	/**
//	 * Sets a location for a template rendering command help.
//	 *
//	 * @param commandTemplate the command template location
//	 */
//	public void setCommandTemplate(String commandTemplate) {
//		this.commandTemplate = commandTemplate;
//	}
//
//	/**
//	 * Sets a location for a template rendering commands help.
//	 *
//	 * @param commandsTemplate the commands template location
//	 */
//	public void setCommandsTemplate(String commandsTemplate) {
//		this.commandsTemplate = commandsTemplate;
//	}
//
//	/**
//	 * Sets if groups should be shown in a listing, defaults to true. If not enabled
//	 * a simple list is shown without groups.
//	 *
//	 * @param showGroups the flag to show groups
//	 */
//	public void setShowGroups(boolean showGroups) {
//		this.showGroups = showGroups;
//	}
//
//	private AttributedString renderCommands() {
//		Map<String, CommandRegistration> registrations = Utils
//				.removeHiddenCommands(getCommandCatalog().getRegistrations());
//
//		boolean isStg = this.commandTemplate.endsWith(".stg");
//
//		Map<String, Object> model = new HashMap<>();
//		model.put("model", GroupsInfoModel.of(this.showGroups, registrations));
//
//		String templateResource = resourceAsString(getResourceLoader().getResource(this.commandsTemplate));
//		return isStg ? this.templateExecutor.renderGroup(templateResource, model)
//				: this.templateExecutor.render(templateResource, model);
//	}
//
//	private AttributedString renderCommand(String command) {
//		Map<String, CommandRegistration> registrations = Utils
//				.removeHiddenCommands(getCommandCatalog().getRegistrations());
//		CommandRegistration registration = registrations.get(command);
//		if (registration == null) {
//			throw new IllegalArgumentException("Unknown command '" + command + "'");
//		}
//
//		boolean isStg = this.commandTemplate.endsWith(".stg");
//
//		Map<String, Object> model = new HashMap<>();
//		model.put("model", CommandInfoModel.of(command, registration));
//
//		String templateResource = resourceAsString(getResourceLoader().getResource(this.commandTemplate));
//		return isStg ? this.templateExecutor.renderGroup(templateResource, model)
//				: this.templateExecutor.render(templateResource, model);
//	}
//
//	private static String resourceAsString(Resource resource) {
//		try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
//			return FileCopyUtils.copyToString(reader);
//		} catch (IOException e) {
//			throw new UncheckedIOException(e);
//		}
//	}
//}
