import React, { useCallback, useEffect, useState } from 'react'

import './movie-gird.scss'
import MovieCard from '../movie-card/MovieCard'
import { useParams } from 'react-router-dom'
import { useNavigate } from 'react-router-dom'
import Input from '../input/Input'
import Button from '../button/Button'
import axios from 'axios'
import FilterSearch from '../filterSearch/FilterSearch'

const MovieGird = props => {
    const options = [
        { label: "Genre", value: "" },
        { label: "Adventure", value: "Adventure" },
        { label: "Comedy", value: "Comedy" },
    ]
    // const data = props.movies
    const navigate = useNavigate();
    const [movies, setMovies] = useState([]);
    const [genre, setGenre] = useState(null);
    const [isSearch, setIsSearch] = useState(null);
    const { keyword } = useParams();
    const { category } = useParams();

    useEffect(() => {
        const getMovies = async () => {
            if (keyword === undefined) {
                try {
                    setMovies(props.movies);
                    setIsSearch(true);
                } catch (err) {
                    console.log(err)
                }
            }
            else {
                try {
                    const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/search/similar-movies`, {
                        params: {
                            movie_title: keyword
                        },
                        headers: {
                            Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                        }
                    });
                    setMovies(res.data.result);
                    
                    setIsSearch(false);
                } catch (err) {
                    console.log(err)
                }
            }
        }
        if (props.movies.length !== 0) getMovies();
    }, [props.movies, keyword])

    const handleSearch = () => {
        const url = `/${category}/${genre}`;
        navigate(url);
    }

    return (
        <>
            <div className='between'>

                <div className='section mb-3'>
                    <span>{category === "Movies" ? "Movies Type" : "Series Type"}</span>
                    <select
                        name='genre'
                        id="genre"
                        onChange={(e) => setGenre(e.target.value)}
                        onClick={() => handleSearch()}
                    >
                        {
                            options.map(option => (
                                <option value={option.value}>{option.label}</option>
                            ))
                        }
                    </select>
                </div>
                <div className='section mb-3'>
                    <MovieSearch category={category} keyword={keyword} data={props.movies} />
                </div>
            </div>
            <div className='movie-grid'>
                        
                {
                    isSearch === true && movies.length !== 0 && movies.map((item) =>
                        <MovieCard id={item.id} />
                    )}
                {
                    isSearch === false && movies.length !== 0 && movies.map((item) =>
                        <MovieCard id={item} />
                    )
                }
            </div>

        </>
    )
}


const MovieSearch = props => {
    const navigate = useNavigate();
    const [keyword, setKeyword] = useState(props.keyword ? props.keyword : '')
    const pathName = props.category;
    const [movie, setMovie] = useState([]);
    const [isSearching, setIsSearching] = useState(false);
    const goToSearch = useCallback(
        () => {
            if (keyword.trim().length > 0) {
                navigate(`/${pathName}/search/${keyword}`);
                
                window.location.reload();
            }
        },
        [keyword, pathName, navigate]
    )

    useEffect(() => {
        const enterEvent = (e) => {
            e.preventDefault();
            if (e.keyCode === 13) {
                goToSearch()
            }
        }
        document.addEventListener('keyup', enterEvent);
        return () => {
            document.removeEventListener('keyup', enterEvent);
        }
    }, [keyword, goToSearch])

    useEffect(() => {
        const getMovies = async () => {
            try {
                const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/search/similar-movies`, {
                    params: {
                        movie_title: keyword
                    },
                    headers: {
                        Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                    }
                });
                setMovie(res.data.result)
            } catch(err) {
                console.log(err);
            }
        }
        if(props.data.length !==0 && keyword.length !== 0 && isSearching === true) {
            setMovie([]);
            getMovies();
        }
        else if(keyword.length === 0) {
            setMovie([]);
        }
    },[keyword,props])
   
    return (
        <div className='body'>

            <div className='movie-search'>
                <Input
                    type="text"
                    placeholder="Enter keyword"
                    value={keyword}
                    onChange={(e) => {
                        setKeyword(e.target.value);
                        setIsSearching(true);
                    }}
                />
                <Button className="small" onClick={goToSearch}>Search</Button>
            </div>
            {
                movie.length !==0 && movie.map((item) => (
                    <FilterSearch id={item}/>
                ))
                
            }
        </div>
    )
}
export default MovieGird
