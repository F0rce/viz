package de.f0rce.viz.events;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;

import de.f0rce.viz.Viz;

/**
 * Event for receiving the uri of the rendered/displayed graph.
 *
 * @author David "F0rce" Dodlek
 */
@DomEvent("viz-image-encode")
public class VizImageEncodeEvent extends ComponentEvent<Viz> {
	private String imageUri;

	public VizImageEncodeEvent(Viz source, boolean fromClient, @EventData("event.detail.imageUri") String imageUri) {
		super(source, fromClient);

		this.imageUri = imageUri;
	}

	/**
	 * Returns the encoded image uri.
	 *
	 * @return {@link String}
	 */
	public String getImageUri() {
		return this.imageUri;
	}

	/**
	 * Returns the cropped image uri as Base64 encoded byte array. If the image uri
	 * does not contain "image/*;base64," the uri will just be decoded to not throw
	 * a null pointer exception.
	 *
	 * @return byte[]
	 */
	public byte[] getImageBase64() {
		if (this.imageUri.equals("")) {
			return null;
		}
		String split = this.imageUri.split(",")[1];
		if (split.length() == 0) {
			return Base64.getDecoder().decode(this.imageUri.getBytes(StandardCharsets.UTF_8));
		}
		return Base64.getDecoder().decode(split.getBytes(StandardCharsets.UTF_8));
	}
}
