USE moviedb; 
SELECT movDesc.title,movDesc.year,movDesc.director,movDesc.rating, stars.name AS 'Actor', genres.name AS 'Genre'
	FROM(SELECT mov.title, mov.year,mov.director,movRatings.rating, mov.id
		FROM movies AS mov, ratings AS movRatings
		WHERE mov.id = movRatings.movieId 
        ORDER BY movRatings.rating DESC 
        LIMIT 20) AS movDesc, genres, stars, stars_in_movies, genres_in_movies
	WHERE movDesc.id = stars_in_movies.movieId AND stars.id = stars_in_movies.starId AND movDesc.id = genres_in_movies.movieId AND  genres.id = genres_in_movies.genreId;
    
    
    


