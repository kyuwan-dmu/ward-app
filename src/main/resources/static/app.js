const infoEndpoint = "/api/info";
const restaurantEndpoint = "/api/restaurants";

function createStars(rating) {
	const fullStars = Math.round(rating);
	return "★★★★★"
		.split("")
		.map((star, index) => (index < fullStars ? star : "☆"))
		.join("");
}

function renderRestaurants(restaurants) {
	const list = document.getElementById("restaurant-list");
	list.innerHTML = restaurants.map((restaurant) => `
		<article class="restaurant-card">
			<div class="thumb" aria-hidden="true"></div>
			<div>
				<div class="restaurant-name">
					${restaurant.name}
					<span class="rating">${createStars(restaurant.rating)}</span>
				</div>
				<div class="restaurant-sub">${restaurant.category}</div>
				<div class="restaurant-summary">${restaurant.summary}</div>
			</div>
			<div class="restaurant-links">
				<a href="#">방문자 리뷰보기 &gt;</a>
				<a href="#">리뷰 작성하기 &gt;</a>
			</div>
		</article>
	`).join("");
}

async function loadInfo() {
	const response = await fetch(infoEndpoint);
	const info = await response.json();

	document.title = info.serviceName;
	document.getElementById("tagline").textContent = info.subtitle;
	document.getElementById("welcome-message").textContent = info.welcomeMessage;
	document.getElementById("location-input").value = info.location;
	document.getElementById("weather-location").textContent = `${info.location}의 날씨`;
	document.getElementById("weather-temperature").textContent = info.temperature;
	document.getElementById("weather-status").textContent = info.weatherStatus;
	document.getElementById("dust-status").textContent = info.dustStatus;
	document.getElementById("feels-like").textContent = info.feelsLike;
}

async function loadRestaurants() {
	const response = await fetch(restaurantEndpoint);
	const restaurants = await response.json();
	renderRestaurants(restaurants);
}

async function bootstrap() {
	try {
		await Promise.all([loadInfo(), loadRestaurants()]);
	} catch (error) {
		console.error("mock page load failed", error);
	}
}

bootstrap();
