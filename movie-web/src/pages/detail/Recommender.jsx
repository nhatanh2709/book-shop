import React from 'react'
import axios from 'axios'
import { useState, useEffect } from 'react'
import MovieList from '../../components/movie-list/MovieList'
const Recommender = props => {
    const [list, setList] = useState([]);
    const Title = props.title
    

    useEffect(() => {
        const findRecommender = async() => {
            try {
                const res = await axios.get(
                    `${process.env.REACT_APP_GATEWAY_URL}/api/v1/movie/flims/random`, {
                        headers: {
                            Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                          }
                    }
                )
                setList(res.data.result);
            } catch (err) {
                console.log(err)
            }
        }
        findRecommender();
    },[])
  return (
    <div>
      {
        list.length !== 0 && <MovieList content = {list.movies}/> 
      }
    </div>
  )
}

export default Recommender
