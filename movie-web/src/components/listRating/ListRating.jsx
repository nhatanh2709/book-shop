import React from 'react'
import axios from 'axios'
import { useState, useEffect } from 'react'
import MovieList from '../movie-list/MovieList'
const ListRating = () => {
    const [ratings, setRatings] = useState([]);
    const [movies, setMovies] = useState([]);
    const [lists, setLists] = useState([])
    useEffect(() => {
        const getRating = async () => {
            try {
                const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/service2/api/ratings/getAll`, {
                    headers: {
                        token: "Bearer "+JSON.parse(localStorage.getItem("user")).accessToken
                    }
                })
                const RatingList = res.data.result;
                const newRatings = RatingList.map(item => item);
                setRatings(newRatings);
            } catch (err) {
                console.log(err)
            }
        }
        const getUser = async () => {
            try {
                const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/service2/api/movies/getInfo`, {
                    headers: {
                        token: "Bearer "+JSON.parse(localStorage.getItem("user")).accessToken
                    }
                })
                const MoviesList = res.data.result;
                const newMovie = MoviesList.map(item => item);
                setMovies(newMovie);
            } catch (err) {
                console.log(err);
            }
        }
        getRating();
        getUser();
    }, [])

    useEffect(() => {
        const findHighestRatingMovies = async () => {
            try {
                const response = await axios.post(`${process.env.REACT_APP_GATEWAY_URL}/service2/api/movies/popular`, {
                    ratings: ratings,
                    movies: movies
                }, {
                    headers: {
                        token: "Bearer "+JSON.parse(localStorage.getItem("user")).accessToken
                    }
                })
                setLists(response.data.result)

            } catch (err) {
                console.log(err)
            }
        }
        if (ratings.length !== 0 && movies.length !== 0) {
            findHighestRatingMovies()
        }
    }, [ratings, movies])
    
    return (
        <>
            <div className='section__header mb-2'>
                <h2>Highest Rating</h2>
            </div>
            { 
                lists.length !== 0 && <MovieList content = {lists}/> 
            }
        </>
    )
}

export default ListRating
