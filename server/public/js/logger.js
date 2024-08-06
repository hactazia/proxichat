export default class Logger {
    static get node() {
        return document.querySelector('#logger');
    }

    static logText(text) {
        let p = document.createElement('p');
        p.classList.add('log');
        p.innerText = text;
        this.node.appendChild(p);
    }

    static logNode(node) {
        let p = document.createElement('p');
        p.classList.add('log');
        p.appendChild(node);
        this.node.appendChild(p);
    }

    static clear() {
        this.node.innerHTML = '';
    }
}