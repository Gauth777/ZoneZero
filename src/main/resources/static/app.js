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
  // News section elements
  newsSection: document.getElementById('newsSection'),
  newsList: document.getElementById('newsList'),
  moreNewsLink: document.getElementById('moreNewsLink'),
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

  try {
    // Send to backend
    const res = await fetch("http://localhost:8080/api/register", {
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

    // Save locally too
    localStorage.setItem("dv_user", JSON.stringify(user));
    enterDashboard(user);
  } catch (err) {
    console.error("Network error:", err);
    alert("Unable to reach server. Is the backend running?");
  }
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
  loadNews(); // automatically load news headlines on dashboard
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

// Simple rule-based warnings
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

// ---- News integration ----
// Fetch the latest disaster news from your backend.
// You’ll need to implement the /api/news endpoint on the server side.
async function getNews() {
  try {
    const res = await fetch(`${API_BASE}/api/news?q=disaster`);
    if (!res.ok) throw new Error(`News fetch failed: ${res.status}`);
    return await res.json(); // Expect array of { title, description, url }
  } catch (e) {
    console.warn("Live news fetch failed; using mock data:", e);
    return [
      {
        title: "Major Floods Impact Southeast Asia",
        description: "Heavy rains have triggered widespread flooding in parts of Thailand and Cambodia...",
        url: "https://example.com/article1"
      },
      {
        title: "Wildfire Season Intensifies in California",
        description: "Multiple blazes are spreading rapidly across northern California as dry conditions persist...",
        url: "https://example.com/article2"
      },
      {
        title: "Earthquake Strikes Near Tokyo, Minor Damage Reported",
        description: "A magnitude 5.4 earthquake shook the outskirts of Tokyo early this morning...",
        url: "https://example.com/article3"
      }
    ];
  }
}

// Automatically load the first two news items on dashboard load
async function loadNews() {
  const articles = await getNews();
  renderNews(articles);
}

// Render the news list into the full-width section
function renderNews(articles) {
  els.newsList.innerHTML = '';
  // Display only the first two headlines
  const showCount = Math.min(articles.length, 2);
  for (let i = 0; i < showCount; i++) {
    const a = articles[i];
    const li = document.createElement('li');
    li.innerHTML = `<a href="${a.url}" target="_blank">${a.title}</a>`;
    els.newsList.appendChild(li);
  }
  // If more than two articles, show the "More news…" link
  if (articles.length > 2) {
    els.moreNewsLink.hidden = false;
    els.moreNewsLink.onclick = (e) => {
      e.preventDefault();
      // Display remaining articles when "More news…" is clicked
      els.newsList.innerHTML = '';
      for (let i = 2; i < articles.length; i++) {
        const a = articles[i];
        const li = document.createElement('li');
        li.innerHTML = `<a href="${a.url}" target="_blank">${a.title}</a>`;
        els.newsList.appendChild(li);
      }
      els.moreNewsLink.hidden = true;
    };
  } else {
    els.moreNewsLink.hidden = true;
  }
}

// Display quiz totals (unchanged)
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
