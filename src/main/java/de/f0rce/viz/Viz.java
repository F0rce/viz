package de.f0rce.viz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;

import de.f0rce.viz.events.VizImageEncodeEvent;

/**
 * @author David "F0rce" Dodlek
 */
@NpmPackage(value = "viz.js", version = "2.1.2")
@NpmPackage(value = "svg-pan-zoom", version = "3.6.1")
@JsModule("./@f0rce/viz-widget/viz-widget.js")
@Tag("lit-viz")
public class Viz extends Div implements HasSize {

	private String graph;
	private boolean showControlIcons = true;
	private VizEngine engine = VizEngine.dot;
	private VizFormat format = VizFormat.svg;
	private String mimeType = "image/png";
	private boolean mouseWheelZoomEnabled = true;

	public Viz() {
		this.setWidth("300px");
		this.setHeight("600px");
	}

	/**
	 * Sets the graph for the widget to render.
	 *
	 * @param graph String
	 */
	public void setGraph(String graph) {
		this.getElement().setAttribute("graph", graph);
		this.graph = graph;
	}

	/**
	 * Sets the graph for the widget to render (read from .dot file).
	 *
	 * @param file File
	 */
	public void setGraph(File file) {
		if (FilenameUtils.getExtension(file.getName()).equals("dot")) {
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append(" ");
				}
				fr.close();
				this.getElement().setAttribute("graph", sb.toString());
				this.graph = sb.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the current set graph.
	 *
	 * @return String
	 */
	public String getGraph() {
		return this.graph;
	}

	/**
	 * In SVG mode {@link #setFormat(VizFormat)} sets if the control icons in the
	 * bottom right corner should be displayed or not.
	 *
	 * @param showControlIcons boolean
	 */
	public void showControlIcons(boolean showControlIcons) {
		this.getElement().setProperty("controlIconsEnabled", showControlIcons);
		this.showControlIcons = showControlIcons;
	}

	/**
	 * Returns if the control icons are enabled/disabled.
	 *
	 * @return boolean
	 */
	public boolean isShowControlIcons() {
		return this.showControlIcons;
	}

	/**
	 * Sets the engine for the renderer.
	 *
	 * @param engine VizEngine
	 */
	public void setEngine(VizEngine engine) {
		this.getElement().setProperty("engine", engine.toString());
		this.engine = engine;
	}

	/**
	 * Returns the current set engine.
	 *
	 * @return VizEngine
	 */
	public VizEngine getEngine() {
		return this.engine;
	}

	/**
	 * Sets the format for the renderer.
	 *
	 * @param format VizFormat
	 */
	public void setFormat(VizFormat format) {
		this.getElement().setProperty("format", format.toString());
		this.format = format;
	}

	/**
	 * Returns the current set format.
	 *
	 * @return VizFormat
	 */
	public VizFormat getFormat() {
		return this.format;
	}

	/**
	 * In IMG mode {@link #setFormat(VizFormat)} sets the MIME-Type for the
	 * renderer. MIME-Type has to start with "image/".
	 *
	 * @param mimeType String
	 */
	public void setMimeType(String mimeType) {
		if (!mimeType.contains("image/")) {
			mimeType = "image/png";
		}
		this.getElement().setProperty("mimeType", mimeType);
		this.mimeType = mimeType;
	}

	/**
	 * Returns the current set MIME-Type.
	 *
	 * @return String
	 */
	public String getMimeType() {
		return this.mimeType;
	}

	/**
	 * In SVG mode {@link #setFormat(VizFormat)} sets if mousewheel-scrooling should
	 * be enabled/disabled.
	 *
	 * @param mouseWheelZoomEnabled boolean
	 */
	public void setMouseWheelZoomEnabled(boolean mouseWheelZoomEnabled) {
		this.getElement().setProperty("mouseWheelZoomEnabled", mouseWheelZoomEnabled);
		this.mouseWheelZoomEnabled = mouseWheelZoomEnabled;
	}

	/**
	 * Returns if mousewheel-scrolling is enabled/disabled.
	 *
	 * @return boolean
	 */
	public boolean isMouseWheelZoomEnabled() {
		return this.mouseWheelZoomEnabled;
	}

	/**
	 * Add this listener if you need t
	 *
	 * @param listener {@link ComponentEventListener}
	 * @return {@link Registration}
	 */
	public Registration addImageEncodeListener(ComponentEventListener<VizImageEncodeEvent> listener) {
		return this.addListener(VizImageEncodeEvent.class, listener);
	}

}
