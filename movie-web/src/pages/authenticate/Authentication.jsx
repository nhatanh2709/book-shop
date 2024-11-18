import { useNavigate } from "react-router-dom";
import { useState, useEffect, useContext } from "react";
import { loginFailure, loginSuccess } from '../authContext/AuthActions';
import { AuthContext } from "../authContext/AuthContext";
import Toastify from 'toastify-js';
export default function Authenticate() {
  const navigate = useNavigate();
  const [isLoggedin, setIsLoggedin] = useState(false);
  const { dispatch } = useContext(AuthContext);
  useEffect(() => {
    const fetchAuthCode = async (authCode) => {
      try {
        const response = await fetch(
          `${process.env.REACT_APP_GATEWAY_URL}/api/v1/identity/auth/outbound/authentication?code=${authCode}`,
          {
            method: "POST",
          }
        );
        const data = await response.json();
  
        if (data.code === 1000) {
          dispatch(loginSuccess(data.result));
          Toastify({
            text: "Login Successfully",
            style: {
              background: "linear-gradient(to right, #00b09b, #96c93d)",
              display: "flex",
              justifyContent: "center",  // Center horizontally
              alignItems: "center",  // Center vertically
            },
          }).showToast();
          navigate("/login");
          setIsLoggedin(true);
        }
      } catch (error) {
        console.error("Error fetching auth code:", error);
      }
    };
  
    console.log(window.location.href);
  
    const authCodeRegex = /code=([^&]+)/;
    const isMatch = window.location.href.match(authCodeRegex);
  
    if (isMatch) {
      const authCode = isMatch[1];
      fetchAuthCode(authCode);
    }
  }, []);

  useEffect(() => {
    if (isLoggedin) {
      navigate("/");
    }
  }, [isLoggedin, navigate]);

  return (
    <>
        
    </>
  );
}