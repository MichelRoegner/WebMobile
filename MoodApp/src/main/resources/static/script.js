window.triggerConfetti = () => {
    const n = 120;
    for (let i = 0; i < n; i++) {
        const s = document.createElement('span');
        s.textContent = '🎉';
        s.style.position = 'fixed';
        s.style.left = Math.random() * 100 + 'vw';
        s.style.top = '-5vh';
        s.style.transition = 'transform 2.2s linear, opacity 2.2s linear';
        document.body.appendChild(s);
        requestAnimationFrame(() => {
            s.style.transform = `translateY(${110 + Math.random()*20}vh) rotate(${Math.random()*720-360}deg)`;
            s.style.opacity = '0';
        });
        setTimeout(() => s.remove(), 2300);
    }
};