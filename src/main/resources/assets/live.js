const webSocket = new WebSocket("ws://localhost:8080/ws");

const initHandlers = () => {
    const elements = document.querySelectorAll('[data-action]');
    elements.forEach((element) => {
        element.onclick = (event) => {
            event.preventDefault();
            const action = element.getAttribute('data-action');
            console.log(`Sending action: ${action}`);
            webSocket.send(action);
        }
    });
}

const elementByPath = (path) => {
    let element = document;
    for (const index of path) {
        element = element.children[index];
    }
    return element;
}

webSocket.onmessage = (event) => {
    console.log(event)
    const diffs = JSON.parse(event.data)
    for (const diff of diffs) {
        switch (diff.type) {
            case 'set-content': {
                const element = elementByPath(diff.path);
                element.textContent = diff.content;
                break;
            }

            case 'set-attribute': {
                const element = elementByPath(diff.path);
                if (diff.value === null) {
                    element.removeAttribute(diff.attr);
                } else {
                    element.setAttribute(diff.attr, diff.value);
                }
                break;
            }

            case 'remove-attribute': {
                const element = elementByPath(diff.path);
                element.removeAttribute(diff.attr);
                break;
            }

            case 'insert': {
                const parentPath = diff.path.slice(0, -1);
                const parent = elementByPath(parentPath);
                const element = document.createElement(diff.tag);
                element.outerHTML = diff.outerHTML;
                parent.appendChild(element);
                break;
            }

            case 'remove': {
                const element = elementByPath(diff.path);
                element.remove();
                break;
            }

            case 'replace': {
                const element = elementByPath(diff.path);
                element.outerHTML = diff.outerHTML;
                break;
            }
        }
    }
    //initHandlers()
}

initHandlers()