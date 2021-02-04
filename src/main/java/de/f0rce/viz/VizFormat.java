package de.f0rce.viz;

/**
 * @author David "F0rce" Dodlek
 */
public enum VizFormat {
	svg, dot, json, xdot, plain, ps, ps2, json0, img, plainExt {
		public String toString() {
			return "plain-ext";
		}
	},
}
