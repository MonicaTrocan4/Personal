import React from 'react'
import {BrowserRouter as Router, Route, Switch, Redirect} from 'react-router-dom'
import NavigationBar from './navigation-bar'
import Login from './auth/login';
import Register from './auth/register';
import AdminDashboard from './admin/admin-dashboard';
import DeviceAdminPage from "./admin/device-admin-page";
import ClientDashboard from './client/client-dashboard';
import ErrorPage from './commons/errorhandling/error-page';
import {jwtDecode} from 'jwt-decode';
import DeviceStatisticsPage from './client/device-statistics-page';

import styles from './commons/styles/project-style.css';

import WebSocketService from './websocket/WebSocketService';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

class App extends React.Component {

    componentDidMount() {
        const token = localStorage.getItem("token");
        if (token) {
            WebSocketService.connect(this.handleNotification);
        }
    }

    componentWillUnmount() {
        WebSocketService.disconnect();
    }

    handleNotification = (message) => {
        console.log("Notificare primita:", message);
        toast.warn(message, {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
        });
    };

    render() {
        return (
            <div className={styles.back}>
                <ToastContainer />

                <Router>
                    <div>
                        <NavigationBar />
                        <Switch>

                            <Route exact path='/' render={() => {
                                const token = localStorage.getItem("token");
                                if (!token) {
                                    return <Redirect to="/login" />;
                                }
                                try {
                                    const decoded = jwtDecode(token);
                                    const role = decoded.role || (decoded.roles && decoded.roles[0]);

                                    WebSocketService.connect(this.handleNotification);

                                    if (role === 'ADMIN') {
                                        return <Redirect to="/admin" />;
                                    } else {
                                        return <Redirect to="/client" />;
                                    }
                                } catch(e) {
                                    localStorage.clear();
                                    return <Redirect to="/login" />;
                                }
                            }}/>

                            <Route exact path='/login' component={Login} />
                            <Route exact path='/register' component={Register} />

                            <Route exact path='/client' render={(props) => {
                                if (!localStorage.getItem("token")) return <Redirect to="/login" />;
                                return <ClientDashboard {...props}/>;
                            }}/>

                            <Route exact path='/device-stats/:id' render={(props) => {
                                if (!localStorage.getItem("token")) return <Redirect to="/login" />;
                                return <DeviceStatisticsPage {...props}/>;
                            }}/>

                            <Route exact path='/admin' render={() => {
                                if (!localStorage.getItem("token")) return <Redirect to="/login" />;
                                return <AdminDashboard/>;
                            }}/>

                            <Route exact path='/admin/devices' render={() => {
                                if (!localStorage.getItem("token")) return <Redirect to="/login" />;
                                return <DeviceAdminPage/>;
                            }}/>

                            <Route component={ErrorPage} />
                        </Switch>
                    </div>
                </Router>
            </div>
        )
    };
}

export default App;