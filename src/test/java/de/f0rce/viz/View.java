package de.f0rce.viz;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class View extends VerticalLayout {

	public View() {
		Button openMe = new Button("OPEN ME :)");

		Dialog mainDialog = new Dialog();
		mainDialog.setSizeFull();
		mainDialog.setCloseOnEsc(true);
		mainDialog.setCloseOnEsc(true);
		mainDialog.setResizable(true);

		openMe.addClickListener(event -> {
			mainDialog.open();
		});

		Viz viz = new Viz();

		viz.addImageEncodeListener(evt -> {
			System.out.println(evt.getImageUri());
		});

		viz.setHeight("400px");
		viz.setWidth("900px");
		viz.setMinHeight("500px");
		viz.showControlIcons(true);
		viz.setMouseWheelZoomEnabled(false);

		TextArea input = new TextArea("Input");
		input.setMaxHeight("200px");
		input.addValueChangeListener(event -> {
			viz.setGraph(event.getSource().getValue());
		});

		ComboBox<VizEngine> engine = new ComboBox<>();
		engine.setItems(VizEngine.values());
		engine.setValue(viz.getEngine());
		engine.setLabel("Engine");
		engine.addValueChangeListener(event -> {
			viz.setEngine(event.getSource().getValue());
		});

		ComboBox<VizFormat> format = new ComboBox<>();
		format.setItems(VizFormat.values());
		format.setValue(viz.getFormat());
		format.setLabel("Format");
		format.addValueChangeListener(event -> {
			viz.setFormat(event.getSource().getValue());
		});

		TextField tf = new TextField("MIME-Type");
		tf.setValue(viz.getMimeType());
		tf.addValueChangeListener(event -> {
			viz.setMimeType(event.getSource().getValue());
		});

		VerticalLayout vL = new VerticalLayout();
		vL.add(viz);

		HorizontalLayout hL = new HorizontalLayout();
		hL.add(input, engine, format, tf);

		mainDialog.add(hL, vL);

		this.add(openMe);
	}
}
