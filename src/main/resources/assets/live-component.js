class LiveComponent extends HTMLElement {
  constructor() {
    super();
  }

  connectedCallback() {
    console.log('LiveComponent connected');
  }
}

customElements.define('live-component', LiveComponent);