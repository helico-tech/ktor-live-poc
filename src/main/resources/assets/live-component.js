class LiveComponent extends HTMLElement {
  constructor() {
    super();
  }

  connectedCallback() {
    const elements = this.querySelectorAll('[action-click]');
    elements.forEach((element) => {
      element.onclick = async (event) => {
        event.preventDefault();
        const action = element.getAttribute('action-click');
        const payload = element.getAttribute(`action-click-payload`);
        await this.#sendAction(action, payload);
      }
    });
  }

  get component() {
    return this.getAttribute('component');
  }

  get endpoint() {
    return this.getAttribute('endpoint');
  }

  get state() {
    const state = {}
    this.getAttributeNames().filter(name => name.startsWith('state-')).forEach(name => {
      state[name.substring(6)] = this.getAttribute(name);
    })
    return state;
  }

  async #sendAction(action, payload) {
    const data = {
      component: this.component,
      action,
      payload,
      state: this.state
    }

    const result = await fetch(this.endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })

    const response = await result.json();

    this.#processDiffs(response);
  }

  #elementByPath(path) {
    let element = this;
    for (const index of path) {
      element = element.children[index];
    }
    return element;
  }

  #processDiffs(diffs) {
    for (const diff of diffs) {
      switch (diff.type) {
        case 'set-content': {
          const element = this.#elementByPath(diff.path);
          element.textContent = diff.content;
          break;
        }

        case 'set-attribute': {
          const element = this.#elementByPath(diff.path);
          if (diff.value === null) {
            element.removeAttribute(diff.attr);
          } else {
            element.setAttribute(diff.attr, diff.value);
          }
          break;
        }

        case 'remove-attribute': {
          const element = this.#elementByPath(diff.path);
          element.removeAttribute(diff.attr);
          break;
        }

        case 'insert': {
          const parentPath = diff.path.slice(0, -1);
          const parent = this.#elementByPath(parentPath);
          const element = document.createElement(diff.tag);
          element.outerHTML = diff.outerHTML;
          parent.appendChild(element);
          break;
        }

        case 'remove': {
          const element = this.#elementByPath(diff.path);
          element.remove();
          break;
        }

        case 'replace': {
          const element = this.#elementByPath(diff.path);
          element.outerHTML = diff.outerHTML;
          break;
        }
      }
    }
  }
}

customElements.define('live-component', LiveComponent);