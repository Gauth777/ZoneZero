// --------- Config ----------
const API_BASE = "http://localhost:8080";

// --------- Elements ----------
const els = {
  loginView: document.getElementById('loginView'),
  dashView: document.getElementById('dashView'),
  loginForm: document.getElementById('loginForm'),
  username: document.getElementById('username'),
  email: document.getElementById('email'),
  phone: document.getElementById('phone'),
  region: document.getElementById('region'),

  greet: document.getElementById('greet'),
  userChip: document.getElementById('userChip'),
  chipName: document.getElementById('chipName'),
  logoutBtn: document.getElementById('logoutBtn'),

  quizBtn: document.getElementById('quizBtn'),
  weatherBtn: document.getElementById('weatherBtn'),
  weatherBtnForceRed: document.getElementById('weatherBtnForceRed'),

  weatherBox: document.getElementById('weatherBox'),
  wTemp: document.getElementById('wTemp'),
  wHum: document.getElementById('wHum'),
  wWind: document.getElementById('wWind'),
  wStat: document.getElementById('wStat'),

  warningsList: document.getElementById('warningsList'),
};

// --------- Helpers ----------
function showError(where, err) {
  console.error(`[${where}]`, err);
  alert(`${where}: ${err?.message || err}`);
}
function assertOk(res) {
  if (!res.ok) throw new Error(`HTTP ${res.status} ${res.statusText}`);
  return res;
}

// --------- Init ----------
document.addEventListener('DOMContentLoaded', async () => {
  try {
    await loadRegions();
  } catch (e) {
    showError('Load Regions', e);
  }

  const raw = localStorage.getItem('zz_user');
  if (raw) enterDashboard(JSON.parse(raw));
});

// --------- Regions ----------
async function loadRegions() {
  const res = await fetch(`${API_BASE}/api/regions`, { credentials: 'omit' }).then(assertOk);
  const regions = await res.json();
  els.region.innerHTML = regions.map(r => `<option value="${r.name}">${r.name}</option>`).join('');
}

// --------- Auth ----------
els.loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  try {
    const username = els.username.value.trim();
    const email = els.email.value.trim();
    const phone = els.phone.value.trim();
    const regionVal = els.region.value;

    if (!username || !email || !phone || !regionVal) {
      alert('Please fill all fields.');
      return;
    }

    const res = await fetch(`${API_BASE}/api/login`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({ username, email, phone, region: regionVal })
    }).then(assertOk);

    const data = await res.json();
    const user = { id: data.userId, username, email, phone, region: data.region };
    localStorage.setItem('zz_user', JSON.stringify(user));
    enterDashboard(user);
  } catch (e2) {
    showError('Login', e2);
  }
});

els.logoutBtn.addEventListener('click', () => {
  localStorage.removeItem('zz_user');
  location.reload();
});

function enterDashboard(user) {
  els.loginView.hidden = true;
  els.dashView.hidden = false;
  els.userChip.hidden = false;
  els.greet.textContent = `Hi, ${user.username || 'User'}`;
  els.chipName.textContent = user.username || 'User';
  showQuizTotalsOnDashboard();
}

// --------- Weather & Risk ----------
els.weatherBtn.addEventListener('click', async () => {
  await fetchWeatherAndRisk(false);
});
els.weatherBtnForceRed.addEventListener('click', async () => {
  await fetchWeatherAndRisk(true);
});

async function fetchWeatherAndRisk(forceRed) {
  try {
    const user = JSON.parse(localStorage.getItem('zz_user') || '{}');
    if (!user.region) {
      alert('Please login first.');
      return;
    }

    const weather = await getWeather(user.region);
    renderWeather(weather);

    const risk = await getRisk(user.region, forceRed);
    renderWarningsFromRisk(risk);
  } catch (e) {
    showError('Fetch Weather/Risk', e);
  }
}

async function getWeather(region) {
  const url = `${API_BASE}/api/weather?region=${encodeURIComponent(region)}`;
  const res = await fetch(url).then(assertOk);
  const j = await res.json();
  return {
    temp: j.temp,
    humidity: j.humidity,
    wind: j.windSpeed,
    status: `${j.source} • r1h=${j.rain1h} r3h=${j.rain3h} pop=${j.pop}`
  };
}

function renderWeather(d) {
  els.weatherBox.hidden = false;
  els.wTemp.textContent = `${d.temp} °C`;
  els.wHum.textContent = `${d.humidity} %`;
  els.wWind.textContent = `${d.wind} m/s`;
  els.wStat.textContent = d.status;
}

async function getRisk(region, forceRed = false) {
  const url = `${API_BASE}/api/risk?region=${encodeURIComponent(region)}${forceRed ? '&force=red' : ''}`;
  const res = await fetch(url).then(assertOk);
  return await res.json(); // {heatLevel,floodLevel,windLevel,messages[],forcedRed}
}

function renderWarningsFromRisk(risk) {
  els.warningsList.innerHTML = '';
  const badge = lvl => `<strong>${lvl.toUpperCase()}</strong>`;
  const rows = [
    {level: risk.heatLevel, text: `Heat level: ${badge(risk.heatLevel)}`},
    {level: risk.floodLevel, text: `Flood level: ${badge(risk.floodLevel)}`},
    {level: risk.windLevel, text: `Wind level: ${badge(risk.windLevel)}`}
  ];

  const dotClass = (lvl) => (lvl === 'red' ? 'red' : (lvl === 'orange' || lvl === 'yellow') ? 'yellow' : 'green');
  let anyRisk = false;

  for (const r of rows) {
    const li = document.createElement('li');
    li.className = 'warn';
    li.innerHTML = `<span class="dot ${dotClass(r.level)}"></span><div>${r.text}</div>`;
    els.warningsList.appendChild(li);
    if (r.level !== 'green') anyRisk = true;
  }

  if (Array.isArray(risk.messages)) {
    for (const m of risk.messages) {
      const li = document.createElement('li');
      li.className = 'warn';
      li.innerHTML = `<span class="dot yellow"></span><div>${m}</div>`;
      els.warningsList.appendChild(li);
    }
  }

  if (!anyRisk && (!risk.messages || risk.messages.length === 0)) {
    els.warningsList.innerHTML = `
      <li class="warn ok"><span class="dot green"></span>
        <div><strong>All clear.</strong> No immediate alerts for your area.</div>
      </li>`;
  }
}

// --------- Quiz placeholder ----------
els.quizBtn.addEventListener('click', () => {
  alert('Quiz UI is a placeholder here. Use backend /api/quiz endpoints or a dedicated page.');
});

// --------- Dashboard quiz badges (local summary only) ----------
function showQuizTotalsOnDashboard() {
  const bestMap = JSON.parse(localStorage.getItem('dv_best_scores_v1') || '{}');
  const total = Object.values(bestMap).reduce((sum, v) => sum + Number(v || 0), 0);
  const last  = localStorage.getItem('dv_quiz_lastScore') ?? '—';

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
