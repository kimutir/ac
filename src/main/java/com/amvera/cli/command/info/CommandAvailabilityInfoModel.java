package com.amvera.cli.command.info;

class CommandAvailabilityInfoModel {

	private boolean available;
	private String reason;

	CommandAvailabilityInfoModel(boolean available, String reason) {
		this.available = available;
		this.reason = reason;
	}

	static CommandAvailabilityInfoModel of(boolean available, String reason) {
		return new CommandAvailabilityInfoModel(available, reason);
	}

	public boolean getAvailable() {
		return available;
	}

	public String getReason() {
		return reason;
	}
}
