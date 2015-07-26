package com.branwenevans.components.wicket;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.branwenevans.components.wicket.dropdown.select.DropdownSelectPanel;

@SuppressWarnings("serial")
public class ExamplePage extends WebPage {

	public ExamplePage(final PageParameters parameters) {
		super(parameters);

		ChoiceRenderer<Role> roleRenderer = new ChoiceRenderer<Role>() {

			@Override
			public String getDisplayValue(Role role) {
				return role.name();
			}
		};

		DropdownSelectPanel<Role> dropdownSelectPanel = new DropdownSelectPanel<Role>("dropdownSelect", Arrays.asList(Role.values()), roleRenderer) {

			@Override
			protected void onFormSubmit(List<Role> list) {
				// TODO Auto-generated method stub

			}

		};

		add(dropdownSelectPanel);
	}

	public enum Role {
		READ, CREATE, EDIT, ADMIN
	}
}
