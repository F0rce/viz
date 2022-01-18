/**
 * @license MIT
 * Copyright 2021 David "F0rce" Dodlek
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import { LitElement, html, css } from "lit-element";

import Viz from "viz.js";
import { Module, render } from "viz.js/full.render.js";

import svgPanZoom from "svg-pan-zoom";

var viz = new Viz({ Module, render });

class LitViz extends LitElement {
  static get properties() {
    return {
      graph: { type: String },
      controlIconsEnabled: { type: Boolean },
      engine: { type: String },
      format: { type: String },
      mimeType: { type: String },
      mouseWheelZoomEnabled: { type: Boolean },
    };
  }

  constructor() {
    super();
    this.controlIconsEnabled = true;
    this.engine = "dot";
    this.format = "svg";
    this.mimeType = "image/png";
    this.mouseWheelZoomEnabled = true;
  }

  static get styles() {
    return css`
      :host {
        display: flex;
        display: -webkit-flex;
        flex-direction: column;
        -webkit-flex-direction: column;
        width: 100%;
        height: 100%;
      }
      #output {
        flex: 1 1 auto;
        -webkit-flex: 1 1 auto;
        position: relative;
        overflow: auto;
      }
      #output svg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
      }
      #output #text {
        font-size: 12px;
        font-family: monaco, courier, monospace;
        white-space: pre;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        overflow: auto;
      }
      #output img {
        display: block;
        margin: 0 auto;
      }
      #output.working svg,
      #output.error svg,
      #output.working #text,
      #output.error #text,
      #output.working img,
      #output.error img {
        opacity: 0.4;
      }
      #output.error #error {
        display: inherit;
      }
      #output #error {
        display: none;
        position: absolute;
        top: 20px;
        left: 20px;
        margin-right: 20px;
        background: red;
        color: white;
        z-index: 1;
      }
    `;
  }

  render() {
    return html`
      <div id="output" class="" style="height: 100%; width: 100%">
        <div id="error"></div>
      </div>
    `;
  }

  firstUpdated(changedProperties) {
    this.parser = new DOMParser();
    this.running = false;

    this.outputDiv = this.shadowRoot.getElementById("output");
    this.errorDiv = this.shadowRoot.getElementById("error");

    this.updateGraph();
    this.updateOutput();
  }

  updated(changedProperties) {
    var allowedCaller = ["graph", "engine", "format", "mimeType"];
    changedProperties.forEach((oldValue, propName) => {
      if (allowedCaller.includes(propName)) {
        this.updateGraph();
      }
    });
  }

  updateGraph() {
    if (this.running) return;
    if (!this.graph) return;

    this.outputDiv.classList.add("working");
    this.outputDiv.classList.remove("error");

    this._graph = this.graph;

    if (this.format == "img") {
      this.running = true;
      viz
        .renderImageElement(this.graph, {
          engine: this.engine,
          mimeType: this.mimeType,
        })
        .then((e) => {
          this.outputDiv.classList.remove("working");
          this.outputDiv.classList.remove("error");

          this.result = e;

          this.updateOutput();
        })
        .catch((e) => {
          viz = new Viz({ Module, render });
          this.outputDiv.classList.remove("working");
          this.outputDiv.classList.add("error");

          var message =
            e.message === undefined
              ? "An error occurred while processing the graph input."
              : e.message;

          while (this.errorDiv.firstChild) {
            this.errorDiv.removeChild(this.errorDiv.firstChild);
          }

          this.errorDiv.appendChild(document.createTextNode(message));

          console.error(e);
          this.running = false;
        });
    } else {
      this.running = true;
      viz
        .renderString(this.graph, { engine: this.engine, format: this.format })
        .then((e) => {
          this.outputDiv.classList.remove("working");
          this.outputDiv.classList.remove("error");

          this.result = e;

          this.updateOutput();
        })
        .catch((e) => {
          viz = new Viz({ Module, render });
          this.outputDiv.classList.remove("working");
          this.outputDiv.classList.add("error");

          var message =
            e.message === undefined
              ? "An error occurred while processing the graph input."
              : e.message;

          while (this.errorDiv.firstChild) {
            this.errorDiv.removeChild(this.errorDiv.firstChild);
          }

          this.errorDiv.appendChild(document.createTextNode(message));

          console.error(e);
          this.running = false;
        });
    }
  }

  updateOutput() {
    var svg = this.outputDiv.querySelector("svg");
    if (svg) {
      this.outputDiv.removeChild(svg);
    }

    var text = this.outputDiv.querySelector("#text");
    if (text) {
      this.outputDiv.removeChild(text);
    }

    var img = this.outputDiv.querySelector("img");
    if (img) {
      this.outputDiv.removeChild(img);
    }

    if (!this.result) {
      return;
    }

    if (this.format == "svg") {
      var svg = this.parser.parseFromString(
        this.result,
        "image/svg+xml"
      ).documentElement;
      svg.id = "svg_output";
      this.outputDiv.appendChild(svg);

      var serialized = new XMLSerializer().serializeToString(svg);
      var base64 = window.btoa(serialized);
      var imageUri = `data:image/svg+xml;base64,${base64}`;

      svgPanZoom(svg, {
        zoomEnabled: true,
        controlIconsEnabled: this.controlIconsEnabled,
        fit: true,
        center: true,
        minZoom: 0.1,
        mouseWheelZoomEnabled: this.mouseWheelZoomEnabled,
      });

      this.dispatchEvent(
        new CustomEvent("viz-image-encode", {
          detail: {
            imageUri: imageUri,
          },
        })
      );
    } else if (this.format == "img") {
      this.outputDiv.appendChild(this.result);

      let self = this;
      toDataURL(this.result.src, function (dataUrl) {
        self.dispatchEvent(
          new CustomEvent("viz-image-encode", {
            detail: {
              imageUri: dataUrl,
            },
          })
        );
      });
    } else {
      var text = document.createElement("div");
      text.id = "text";
      text.appendChild(document.createTextNode(this.result));
      this.outputDiv.appendChild(text);
    }

    this.running = false;
    if (this._graph !== this.graph) {
      this.updateGraph();
    }
  }
}

customElements.define("lit-viz", LitViz);

// Utility
function toDataURL(url, callback) {
  var xhr = new XMLHttpRequest();
  xhr.onload = function () {
    var reader = new FileReader();
    reader.onloadend = function () {
      callback(reader.result);
    };
    reader.readAsDataURL(xhr.response);
  };
  xhr.open("GET", url);
  xhr.responseType = "blob";
  xhr.send();
}
