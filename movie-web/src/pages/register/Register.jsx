import { useState, useRef } from "react";
import axios from 'axios'
import { FaUser, FaLock } from 'react-icons/fa'
import { SiGmail } from 'react-icons/si'
import { Link } from "react-router-dom";
import { useNavigate } from 'react-router-dom'
import "./register.scss";
import Toastify from 'toastify-js';
export default function Register() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [username, setUsername] = useState("");
    const navigate = useNavigate();

 
    const handleRegister = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post(`http://${process.env.REACT_APP_GATEWAY_HOST}:${process.env.REACT_APP_GATEWAY_PORT}/api/v1/identity/users/registration`, { email, username, password });
            Toastify({
                text: "Register Successfully",
                style: {
                  background: "linear-gradient(to right, #00b09b, #96c93d)",
                  display : "flex",
                  justifyContent: "center",  // Căn giữa theo chiều ngang
                  alignItems: "center",
                },
              }).showToast();
            navigate("/login");
        } catch (err) {
           
            Toastify({
                text: err.response.data.message,
                style: {
                  background: "red",
                  display : "flex",
                  justifyContent: "center",  // Căn giữa theo chiều ngang
                  alignItems: "center",
                },
              }).showToast();
        }
        
        
    };
    return (
        <div className="login">
            <div className="body">
                <div className="wrapper">
                    <form action="">
                        <h1>Register</h1>
                        <div className="input-box">
                            <input 
                            type="email" 
                            required
                            placeholder="email address" 
                            onChange={(e) => setEmail(e.target.value)} />
                            <SiGmail className="icon" />
                        </div>

                        <div className="input-box">
                            <input 
                            type="username" 
                            required
                            placeholder="username" 
                            onChange={(e) => setUsername(e.target.value)} />
                            <FaUser className="icon" />
                        </div>

                        <div className="input-box">
                            <input 
                            type="password" 
                            placeholder="password" 
                            required 
                            onChange={(e) => setPassword(e.target.value)} />
                            <FaLock className="icon" />
                        </div>


                        <div className="remember-forgot">
                            <label>
                                <input type="checkbox" />Remember me
                            </label>
                            <a href="#">
                                <Link to="/newPassword">
                                    Forgot Password?
                                </Link>
                            </a>
                        </div>
                        <button type="submit" onClick={handleRegister}>
                            Register
                        </button>

                        <div className="register-link">
                            <p>Have an account ?
                                <a href="#">
                                    <Link to="/login">
                                        Login
                                    </Link>
                                </a>
                            </p>
                        </div>
                        <div className="register-link">
                            <p>Continue with Google ?
                                <a href="#">
                                    <Link to="/loginAI">
                                        Login
                                    </Link>
                                </a>
                            </p>
                        </div>
                        
                    </form>
                </div>
            </div>
        </div>
    );
}