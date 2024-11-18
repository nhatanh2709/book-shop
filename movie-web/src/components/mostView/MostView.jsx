import React from 'react'
import './mostView.scss'
import { SwiperSlide, Swiper } from 'swiper/react'
import MovieCard from '../movie-card/MovieCard'
import { useState, useEffect } from 'react'
import axios from 'axios'
const MostView = () => {
    const [movies, setMovies] = useState([]);
    useEffect(() => {
        const getMovie = async () => {
            try {
                const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/movie/flims/view/mostView`, {
                    headers: {
                        Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                      }
                });
                setMovies(res.data.result.movies);
            } catch (err) {
                console.log(err)
            }
        }
        getMovie();
    }, [])

    return (
        <>
        <div className='section__header mb-2'>
            <h2>Most View</h2>
        </div>
        <div className='movie-list'>
            <Swiper
                grabCursor={true}
                spaceBetween={10}
                slidesPerView={'auto'}
            >
                {
                    movies.map((item, i) => (
                        
                        <SwiperSlide key={i}>
                            <MovieCard id={item.id} />
                        </SwiperSlide>
                    ))
                }

            </Swiper>
        </div>
        
        </>
    )
}

export default MostView
