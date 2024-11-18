import React, { useState, useEffect,  } from 'react';

import SwiperCore, { Autoplay } from 'swiper';
import { Swiper, SwiperSlide } from 'swiper/react';
import axios from 'axios';
import {useNavigate} from 'react-router-dom'
import './hero-slide.scss';
import Button from '../button/Button';
const HeroSlide = () => {

    SwiperCore.use([Autoplay]);

    const [movieItems, setMovieItems] = useState([]);

    useEffect(() => {
        const getRandomContent = async () => {
            try {
                const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/movie/flims/findAll`,{
                    headers: {
                        Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                      }
                  });
                setMovieItems(res.data.result);
            }
            catch(err) 
            {
                console.log(err)
            }
        }
        getRandomContent();
    }, []);

    

    return (
        <div className="hero-slide">
            <Swiper
                modules={[Autoplay]}
                grabCursor={true}
                spaceBetween={0}
                slidesPerView={1}
                //autoplay={{delay: 3000}}
            >
                {
                    movieItems.map((item, i) => (
                        <SwiperSlide key={i}>
                            {({ isActive }) => (
                                <HeroSlideItem item={item} className={`${isActive ? 'active' : ''}`} />
                            )}
                        </SwiperSlide>
                    ))
                }
            </Swiper>
           
        </div>
    );
}
const HeroSlideItem = props => {

    const navigate = useNavigate();
    const item = props.item;
    return (
        <div
            className={`hero-slide__item ${props.className}`}
            style={{backgroundImage: `url(${item.img})`}}
        >
            <div className="hero-slide__item__content container">
                <div className="hero-slide__item__content__info">
                    <h2 className="title">{item.title}</h2>
                    <div className="overview">{item.desc}</div>
                    <Button onClick={() => navigate('/flim/' + item.slug)}>
                            Watch now
                    </Button>
                </div>
                <div className="hero-slide__item__content__poster">
                    <img src={item.img} alt="" />
                </div>
            </div>
        </div>
    )
}



export default HeroSlide