package com.branwenevans.components.wicket.dropdown.select;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

@SuppressWarnings("serial")
public abstract class DropdownSelectPanel<T extends Serializable> extends Panel {

	private ChoiceRenderer<T> renderer;

	private List<SelectableItem<T>> allChoices;

	/**
	 * 
	 * @param id
	 *            The wicket id
	 * @param allChoices
	 *            All of the items which will be available in the dropdowns
	 * @param renderer
	 *            The renderer for displaying items
	 */
	public DropdownSelectPanel(String id, List<T> allChoices, ChoiceRenderer<T> renderer) {
		super(id);
		this.renderer = renderer;
		this.allChoices = initializeAllChoices(allChoices, new ArrayList<T>());
	}

	/**
	 * 
	 * @param id
	 *            The wicket id
	 * @param allChoices
	 *            All of the items which will be available in the dropdown,
	 *            including those already selected
	 * @param renderer
	 *            The renderer for displaying items
	 * @param selectedChoices
	 *            Items which can appear in the dropdown but are pre-selected
	 */
	public DropdownSelectPanel(String id, List<T> allChoices, List<T> selectedChoices, ChoiceRenderer<T> renderer) {
		super(id);
		this.renderer = renderer;
		this.allChoices = initializeAllChoices(allChoices, selectedChoices);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Model<SelectableItem<T>> dropDownSelectedItem = new Model<>();

		Form<Void> form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				onFormSubmit(getSelectedItems());
				super.onSubmit();
			}

		};
		add(form);

		WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupPlaceholderTag(true);
		form.add(container);

		DropDownChoice<SelectableItem<T>> dropdown = new DropDownChoice<>("dropdown", dropDownSelectedItem, getDropdownChoices(), getChoiceRenderer());

		dropdown.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				selectItem(dropDownSelectedItem);
				onItemSelected(dropDownSelectedItem);
				dropDownSelectedItem.setObject(null);
				target.add(container);
			}

		});
		container.add(dropdown);

		container.add(new ListView<SelectableItem<T>>("selectedList", getSelectedItemsModel()) {

			@SuppressWarnings("unchecked")
			@Override
			protected void populateItem(ListItem<SelectableItem<T>> item) {
				item.add(new Label("itemLabel", renderer.getDisplayValue((T) item.getModelObject().item).toString()));
				item.add(new AjaxLink<Void>("deleteLink") {

					@Override
					public void onClick(AjaxRequestTarget target) {
						removeItem(item.getModel());
						onItemRemoved(item.getModel());
						target.add(container);
					}

				});
			}

		});

	}

	public List<T> getSelectedItems() {
		return allChoices.stream().filter(x -> x.selected).map(x -> x.item).collect(toList());
	}

	private ChoiceRenderer<DropdownSelectPanel<T>.SelectableItem<T>> getChoiceRenderer() {
		return new ChoiceRenderer<SelectableItem<T>>() {
			public Object getDisplayValue(SelectableItem<T> selectable) {
				return renderer.getDisplayValue(selectable.item);
			};
		};
	}

	private ListModel<SelectableItem<T>> getDropdownChoices() {
		return new ListModel<SelectableItem<T>>() {
			@Override
			public List<SelectableItem<T>> getObject() {
				return allChoices.stream().filter(x -> !x.selected).collect(toList());
			}
		};
	}

	private List<SelectableItem<T>> initializeAllChoices(List<T> choices, List<T> selectedChoices) {
		return choices.stream().map(x -> new SelectableItem<>(x, selectedChoices.contains(x))).collect(toList());
	}

	private void selectItem(Model<SelectableItem<T>> selectableModel) {
		selectableModel.getObject().selected = true;
	}

	private void removeItem(IModel<SelectableItem<T>> selectableModel) {
		selectableModel.getObject().selected = false;
	}

	private ListModel<SelectableItem<T>> getSelectedItemsModel() {
		return new ListModel<SelectableItem<T>>() {

			@Override
			public List<SelectableItem<T>> getObject() {
				return allChoices.stream().filter(x -> x.selected).collect(toList());
			}
		};
	}

	@SuppressWarnings("hiding")
	protected class SelectableItem<T> implements Serializable {

		private T item;

		private boolean selected;

		protected SelectableItem(T item, boolean selected) {
			this.item = item;
			this.selected = selected;
		}
	}

	protected void onItemSelected(IModel<SelectableItem<T>> item) {

	};

	protected void onItemRemoved(IModel<SelectableItem<T>> item) {

	};

	protected abstract void onFormSubmit(List<T> selectedItems);
}
