import React from 'react'
import "./rating.scss"
import { useState, useEffect } from 'react';
import axios from 'axios'
const Rating = props => {
    const [rating, setRating] = useState(0);
    const user = localStorage.getItem("user");
    const movieId = props.id;

    useEffect(() => {
        const getRating = async () => {
            try {
                const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/movie/ratings/get/` + movieId,  
                    {
                        headers: {
                            Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                        }
                    });
                    console.log(res.data);
                setRating(res.data.result.ratings)
            } catch(err) {
                console.log(err)
            }
        }
        if(movieId !== undefined) {
            getRating();
        }
    }, [movieId]);
    useEffect(() => {
        const newRating = async () => {
            try {
                console.log(rating);
                await axios.post(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/movie/ratings/update`, {
                    movieId: movieId,
                    ratings: rating,
                },{
                    headers: {
                        Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                    }
                })
            } catch (err) {
                console.log(err)
            }
        }
        if(rating) newRating()
    },[ movieId, rating])
    const handleStarClick = (starRating) => {
        setRating(starRating);
    };

    return (
        <div className='cover'>
            <div className="ratings-wrapper">
                <div className="ratings">
                    {[5, 4, 3, 2, 1].map((star) => (
                        <span
                            key={star}
                            data-rating={star}
                            className={star <= rating ? 'rated' : ''}
                            onClick={() => handleStarClick(star)}
                        >
                            ★
                        </span>
                    ))}
                </div>
            </div>
        </div>
    )
}

export default Rating
