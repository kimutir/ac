package com.amvera.cli.custom;

import org.springframework.util.StringUtils;

import java.util.List;

class CommandParameterInfoModel {

	private final String type;
	private final List<String> arguments;
	private boolean required;
	private String description;
	private String defaultValue;

	CommandParameterInfoModel(String type, List<String> arguments, boolean required, String description,
                              String defaultValue) {
		this.type = type;
		this.arguments = arguments;
		this.required = required;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	/**
	 * Builds {@link CommandParameterInfoModel}.
	 *
	 * @param type the type
	 * @param arguments the arguments
	 * @param required the required flag
	 * @param description the description
	 * @param defaultValue the default value
	 * @return a command parameter info model
	 */
	static CommandParameterInfoModel of(String type, List<String> arguments, boolean required,
			String description, String defaultValue) {
		return new CommandParameterInfoModel(type, arguments, required, description, defaultValue);
	}

	public String getType() {
		return type;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public boolean getRequired() {
		return required;
	}

	public String getDescription() {
		return description;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public boolean getHasDefaultValue() {
		return StringUtils.hasText(this.defaultValue);
	}
}
