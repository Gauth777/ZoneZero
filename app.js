// Minimal front-end state
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
};

// Show/hide custom region input
els.region.addEventListener('change', () => {
  const isCustom = els.region.value === 'Custom';
  els.customRegionWrap.hidden = !isCustom;
  if (isCustom) els.customRegion.focus();
});

// Restore session if present
(function init() {
  const raw = localStorage.getItem('dv_user');
  if (raw) {
    const user = JSON.parse(raw);
    enterDashboard(user);
  }
})();

els.loginForm.addEventListener('submit', (e) => {
  e.preventDefault();
  const name = els.username.value.trim();
  const email = els.email.value.trim();
  const phone = els.phone.value.trim();
  const regionSel = els.region.value;
  const region =
    regionSel === 'Custom'
      ? els.customRegion.value.trim()
      : regionSel;

  if (!name || !email || !phone || !region) return;

  const user = { name, email, phone, region };
  localStorage.setItem('dv_user', JSON.stringify(user));
  enterDashboard(user);
});

els.logoutBtn.addEventListener('click', () => {
  localStorage.removeItem('dv_user');
  location.reload();
});

function enterDashboard(user) {
  els.loginView.hidden = true;
  els.dashView.hidden = false;
  els.userChip.hidden = false;
  els.greet.textContent = `Hi, ${user.name}`;
  els.chipName.textContent = user.name;
  
showQuizTotalsOnDashboard();
}

// Quiz placeholder
document.getElementById('quizBtn')?.addEventListener('click', () => {
  window.location.href = 'quiz/index.html';
});


// Weather fetcher
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

// ---- Weather integration ----
// Option A) Live API: Plug your OpenWeatherMap key
// const OPENWEATHER_KEY = "YOUR_KEY";
// async function getWeather(region) {
//   const url = `https://api.openweathermap.org/data/2.5/weather?q=${encodeURIComponent(region)}&appid=${OPENWEATHER_KEY}&units=metric`;
//   const res = await fetch(url);
//   if (!res.ok) throw new Error('Weather fetch failed');
//   const j = await res.json();
//   return {
//     temp: j.main.temp,
//     humidity: j.main.humidity,
//     wind: j.wind.speed, // m/s
//     status: j.weather?.[0]?.main || '—'
//   };
// }

// Option B) Mock (default): quick demo without API
/*async function getWeather(region) {
  // Simple seeded pseudo-random by region string
  let seed = 0;
  for (let i = 0; i < region.length; i++) seed += region.charCodeAt(i);
  const rand = (min, max) => {
    const x = Math.sin(seed++) * 10000;
    return Math.floor((x - Math.floor(x)) * (max - min + 1) + min);
  };
  const temp = rand(22, 36);       // °C
  const humidity = rand(40, 95);   // %
  const wind = rand(1, 20);        // m/s
  const status = ['Clear','Clouds','Rain','Thunderstorm','Drizzle','Haze'][rand(0,5)];
  await new Promise(r => setTimeout(r, 400)); // tiny UX delay
  return { temp, humidity, wind, status };
}
  */

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

const API_BASE = "http://localhost:8080";

async function getWeather(region) {
  try {
    const res = await fetch(`${API_BASE}/api/weather?q=${encodeURIComponent(region)}`);
    if (!res.ok) throw new Error(`Weather fetch failed: ${res.status}`);
    const j = await res.json();
    return {
      temp: j?.main?.temp ?? 0,
      humidity: j?.main?.humidity ?? 0,
      wind: j?.wind?.speed ?? 0,   // m/s
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



// Simple rule-based warnings (tweak thresholds per your idea)
function renderWarnings(d) {
  const rules = [
    {
      test: () => d.wind >= 14,
      text: 'High winds detected. Secure loose items and avoid open areas.',
      level: 'red'
    },
    {
      test: () => d.humidity >= 85 && (d.status === 'Rain' || d.status === 'Thunderstorm'),
      text: 'Heavy moisture + rain: flash-flood risk in low-lying zones.',
      level: 'red'
    },
    {
      test: () => d.temp >= 34 && d.humidity >= 60,
      text: 'Heat + humidity: risk of heat exhaustion. Hydrate, avoid peak sun.',
      level: 'yellow'
    },
    {
      test: () => d.status === 'Thunderstorm',
      text: 'Thunderstorms nearby. Stay indoors and unplug sensitive devices.',
      level: 'yellow'
    },
  ];

  const hits = rules.filter(r => r.test());
  els.warningsList.innerHTML = '';

  if (hits.length === 0) {
    els.warningsList.innerHTML = `
      <li class="warn ok"><span class="dot green"></span>
        <div><strong>All clear.</strong> No immediate alerts for your area.</div>
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

function showQuizTotalsOnDashboard() {
  // read best map (per-scenario) saved by quiz
  const bestMap = JSON.parse(localStorage.getItem('dv_best_scores_v1') || '{}');
  const total = Object.values(bestMap).reduce((sum, v) => sum + Number(v || 0), 0);
  const last  = localStorage.getItem('dv_quiz_lastScore') ?? '—';

  // pick a place to render (try hero first, then header)
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



