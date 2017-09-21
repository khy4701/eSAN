package com.kt.restful.model;

public enum ApiDefine {
	PLTE_ACT("plteact"),
	PLTE_ACT_CONFIRM("plteactconfirm"),
	PLTE_STATUS("pltestatus"),
	PLTE_STATUS_CONFIRM("pltestatusconfirm"),
	PLTE_REG_ESAN("register_esan"),
	
	GET_TOKEN("get-token"),
	DEV_SUBS_LIST("device-subscriber-list"),
	DEV_SUBS("device-subscriber"),
	DEV_SUBS_HISTORY("device-subscriber-history"),
	
	PUSH_TRIGGER("http://%s:%d/api/app/plteact?mdn=%s&authKey=%s&plteMode=ON");

	final private String name;

	private ApiDefine(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

