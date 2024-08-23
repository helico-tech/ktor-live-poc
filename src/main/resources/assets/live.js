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

webSocket.onmessage = (event) => {

    console.log("Morphing")

    const root = document.documentElement;
    Idiomorph.morph(root, event.data, { morphStyle: 'outerHTML' })

    console.log("Morphed")
    initHandlers()
}

initHandlers()