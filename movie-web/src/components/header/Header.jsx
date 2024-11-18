import React, { useRef, useEffect, useContext , useState} from 'react';

import { Link, useLocation } from 'react-router-dom';
import { AuthContext } from '../../pages/authContext/AuthContext';
import './header.scss';
import pic from "../../assets/png1.png"
import logo from '../../assets/logo.png';
import axios from 'axios';

const headerNav = [
    {
        display: 'Home',
        path: '/'
    },
    {
        display: 'Movies',
        path: '/movies'
    },
    {
        display: 'TV Series',
        path: '/series'
    }
];

const Header = () => {

    const { pathname } = useLocation();
    const headerRef = useRef(null);
    const active = headerNav.findIndex(e => e.path === pathname);
    const [info, setInfo] = useState([]);
    useEffect(() => {
        const getInfo = async () => {
          const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/profile/users/myProfile`, {
            headers: {
              Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
            }
          });
          setInfo(res.data.result);
        }
        getInfo();
      },[]);
    useEffect(() => {
        const shrinkHeader = () => {
            if (document.body.scrollTop > 100 || document.documentElement.scrollTop > 100) {
                headerRef.current.classList.add('shrink');
            } else {
                headerRef.current.classList.remove('shrink');
            }
        }
        window.addEventListener('scroll', shrinkHeader);
        return () => {
            window.removeEventListener('scroll', shrinkHeader);
        };
    }, []);

    return (
        <div ref={headerRef} className="header">
            <div className="header__wrap container">
                <div className="logo">
                    
                    <Link to="/">
                        <img src={logo} alt="" />
                    </Link>
                </div>
                <ul className="header__nav">
                    {
                        headerNav.map((e, i) => (
                            <li key={i} className={`${i === active ? 'active' : ''}`}>
                                <Link to={e.path}>
                                    {e.display}
                                </Link>
                            </li>
                        ))
                    }
                    <div className="logo">
                    
                    <Link to="/profile">
                    <img src={info.profilePic || pic} alt="" />
                    </Link>
                    </div>
                </ul>
                
            </div>
        </div>
    );
}

export default Header;