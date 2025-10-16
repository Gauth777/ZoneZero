// ---- Base API ----
const API_BASE = "http://localhost:8080";

console.log("🚀 ZoneZero JS loaded");

// ---- Minimal front-end state ----
const els = {
  loginView: document.getElementById('loginView'),
  dashView: document.getElementById('dashView'),
  loginForm: document.getElementById('loginForm'),
  username: document.getElementById('username'),
  email: document.getElementById('email'),
  phone: document.getElementById('phone'),
  region: document.getElementById('region'),
  customRegionWrap: document.getElementById('customRegionWrap'),
  customRegion: document.getElementById('customRegion'),
  greet: document.getElementById('greet'),
  userChip: document.getElementById('userChip'),
  chipName: document.getElementById('chipName'),
  logoutBtn: document.getElementById('logoutBtn'),
  quizBtn: document.getElementById('quizBtn'),
  weatherBtn: document.getElementById('weatherBtn'),
  weatherBox: document.getElementById('weatherBox'),
  wTemp: document.getElementById('wTemp'),
  wHum: document.getElementById('wHum'),
  wWind: document.getElementById('wWind'),
  wStat: document.getElementById('wStat'),
  warningsList: document.getElementById('warningsList'),
  newsSection: document.getElementById('newsSection'),
  newsList: document.getElementById('newsList'),
  moreNewsLink: document.getElementById('moreNewsLink'),
};

// ---- Show/hide custom region input ----
els.region.addEventListener('change', () => {
  const isCustom = els.region.value === 'Custom';
  els.customRegionWrap.hidden = !isCustom;
  if (isCustom) els.customRegion.focus();
});

// ---- Restore session if present ----
(function init() {
  const raw = localStorage.getItem('dv_user');
  if (raw) {
    const user = JSON.parse(raw);
    enterDashboard(user);
  }
})();

// ---- Submit form handler ----
els.loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();

  const name = els.username.value.trim();
  const email = els.email.value.trim();
  const phone = els.phone.value.trim();
  const regionSel = els.region.value;
  const region =
    regionSel === 'Custom'
      ? els.customRegion.value.trim()
      : regionSel;

  if (!name || !email || !phone || !region) {
    alert("Please fill all fields before entering Zone Zero!");
    return;
  }

  const user = { name, email, phone, region };
  console.log("📡 Attempting registration for:", user);

  try {
    const res = await fetch(`${API_BASE}/api/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(user),
    });

    if (!res.ok) {
      const msg = await res.text();
      console.error("Registration failed:", msg);
      alert("Error while saving user. Please try again.");
      return;
    }

    console.log("✅ Registered successfully:", await res.clone().json());

    localStorage.setItem("dv_user", JSON.stringify(user));
    enterDashboard(user);
  } catch (err) {
    console.error("Network error:", err);
    alert("Unable to reach server. Is the backend running?");
  }
});

// ---- Logout ----
els.logoutBtn.addEventListener('click', () => {
  localStorage.removeItem('dv_user');
  location.reload();
});

// ---- Dashboard view ----
function enterDashboard(user) {
  els.loginView.hidden = true;
  els.dashView.hidden = false;
  els.userChip.hidden = false;
  els.greet.textContent = `Hi, ${user.name}`;
  els.chipName.textContent = user.name;

  showQuizTotalsOnDashboard();
  loadNews();
  checkAlerts(user.region);

}

// ---- Quiz redirect ----
document.getElementById('quizBtn')?.addEventListener('click', () => {
  window.location.href = 'quiz/index.html';
});

// ---- Weather ----
els.weatherBtn.addEventListener('click', async () => {
  const user = JSON.parse(localStorage.getItem('dv_user') || '{}');
  if (!user.region) {
    alert('Please set your region first.');
    return;
  }
  const data = await getWeather(user.region);
  renderWeather(data);
  renderWarnings(data);
});

function getWeatherMock(region) {
  let seed = 0;
  for (let i = 0; i < region.length; i++) seed += region.charCodeAt(i);
  const rand = (min, max) => {
    const x = Math.sin(seed++) * 10000;
    return Math.floor((x - Math.floor(x)) * (max - min + 1) + min);
  };
  const temp = rand(22, 36), humidity = rand(40, 95), wind = rand(1, 20);
  const status = ['Clear','Clouds','Rain','Thunderstorm','Drizzle','Haze'][rand(0,5)];
  return { temp, humidity, wind, status };
}

async function getWeather(region) {
  try {
    const res = await fetch(`${API_BASE}/api/weather?q=${encodeURIComponent(region)}`);
    if (!res.ok) throw new Error(`Weather fetch failed: ${res.status}`);
    const j = await res.json();
    return {
      temp: j?.main?.temp ?? 0,
      humidity: j?.main?.humidity ?? 0,
      wind: j?.wind?.speed ?? 0,
      status: j?.weather?.[0]?.main ?? "—"
    };
  } catch (e) {
    console.warn("Live weather failed; using mock:", e);
    return getWeatherMock(region);
  }
}

function renderWeather(d) {
  els.weatherBox.hidden = false;
  els.wTemp.textContent = `${d.temp} °C`;
  els.wHum.textContent = `${d.humidity} %`;
  els.wWind.textContent = `${d.wind} m/s`;
  els.wStat.textContent = d.status;
}

// ---- Warnings ----
function renderWarnings(d) {
  const rules = [
    { test: () => d.wind >= 14, text: 'High winds detected.', level: 'red' },
    { test: () => d.humidity >= 85 && (d.status === 'Rain' || d.status === 'Thunderstorm'),
      text: 'Flash-flood risk.', level: 'red' },
    { test: () => d.temp >= 34 && d.humidity >= 60,
      text: 'Heat + humidity: stay hydrated.', level: 'yellow' },
    { test: () => d.status === 'Thunderstorm',
      text: 'Thunderstorms nearby.', level: 'yellow' },
  ];

  const hits = rules.filter(r => r.test());
  els.warningsList.innerHTML = '';

  if (hits.length === 0) {
    els.warningsList.innerHTML = `
      <li class="warn ok"><span class="dot green"></span>
        <div><strong>All clear.</strong> No immediate alerts.</div>
      </li>`;
    return;
  }

  for (const h of hits) {
    const dotClass = h.level === 'red' ? 'red' : 'yellow';
    const li = document.createElement('li');
    li.className = 'warn';
    li.innerHTML = `<span class="dot ${dotClass}"></span><div>${h.text}</div>`;
    els.warningsList.appendChild(li);
  }
}

// ---- News ----
async function getNews() {
  try {
    const res = await fetch(`${API_BASE}/api/news?q=disaster`);
    if (!res.ok) throw new Error(`News fetch failed: ${res.status}`);
    return await res.json();
  } catch (e) {
    console.warn("Live news fetch failed; using mock data:", e);
    return [
      { title: "Major Floods Impact Southeast Asia", url: "#1" },
      { title: "Wildfire Season Intensifies in California", url: "#2" },
      { title: "Earthquake Near Tokyo", url: "#3" }
    ];
  }
}

async function loadNews() {
  const articles = await getNews();
  renderNews(articles);
}

function renderNews(articles) {
  els.newsList.innerHTML = '';
  const showCount = Math.min(articles.length, 2);
  for (let i = 0; i < showCount; i++) {
    const a = articles[i];
    const li = document.createElement('li');
    li.innerHTML = `<a href="${a.url}" target="_blank">${a.title}</a>`;
    els.newsList.appendChild(li);
  }
}

// ---- Quiz totals ----
function showQuizTotalsOnDashboard() {
  const bestMap = JSON.parse(localStorage.getItem('dv_best_scores_v1') || '{}');
  const total = Object.values(bestMap).reduce((sum, v) => sum + Number(v || 0), 0);
  const last = localStorage.getItem('dv_quiz_lastScore') ?? '—';

  const target =
    document.querySelector('.hero') ||
    document.querySelector('.nav') ||
    document.querySelector('.app-header');

  if (!target) return;

  let chip = document.getElementById('dvScoreChip');
  const html = `
    <div class="muted">Quiz</div>
    <div><strong>Last:</strong> ${last} | <strong>Total:</strong> ${total}</div>
  `;

  if (!chip) {
    chip = document.createElement('div');
    chip.id = 'dvScoreChip';
    chip.className = 'card';
    chip.style.marginLeft = '12px';
    chip.innerHTML = html;
    target.appendChild(chip);
  } else {
    chip.innerHTML = html;
  }
}

async function checkAlerts(region) {
  try {
    const res = await fetch(`${API_BASE}/api/alerts?region=${encodeURIComponent(region)}`);
    if (!res.ok) throw new Error("Alert fetch failed");
    const alerts = await res.json();

    if (alerts.length > 0) {
      alerts.forEach(a => showAlertPopup(a.message, a.severity));
    }
  } catch (err) {
    console.warn("No alerts found or server unavailable.");
  }
}

function showAlertPopup(message, severity) {
  const div = document.createElement('div');
  div.className = `alert-popup ${severity.toLowerCase()}`;
  div.innerHTML = `<strong>${severity} ALERT:</strong> ${message}`;
  document.body.appendChild(div);
  setTimeout(() => div.remove(), 8000);
}

