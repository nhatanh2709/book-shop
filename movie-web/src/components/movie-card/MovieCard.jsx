import React, { useEffect, useState } from 'react'
import './movie-card.scss';
import axios from 'axios';
import { Link } from 'react-router-dom';
import Button from '../button/Button';
const MovieCard = props => {
  const [movie, setMovie] = useState();

  useEffect(() => {
    const getMovie = async () => {
      try {
        const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/movie/flims/find/` + props.id , {
          headers: {
            Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
          }
          }
        );
  
        setMovie(res.data.result);
      }
      catch (err) {
        console.log(err)
      }
    }
    if(props.id !== undefined) {
      getMovie();
    }
  }, [props]);
  const link = '/flim/' +  movie?.slug;
  
  return (
    <div>
      <Link to={link}>
        <div className="movie-card" style={{backgroundImage: `url(${movie?.img})`}}>
            <Button>
                <i className='bx bx-play' ></i>
            </Button>
        </div>
        <h3>{movie?.title}</h3>
    </Link>
    </div>
  )
}

export default MovieCard
