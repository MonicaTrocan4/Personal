import React from 'react'
import {BrowserRouter as Router, Route, Switch, Redirect} from 'react-router-dom'
import NavigationBar from './navigation-bar'
import Login from './auth/login';
import Register from './auth/register';
import AdminDashboard from './admin/admin-dashboard';
import ErrorPage from './commons/errorhandling/error-page';
import {jwtDecode} from 'jwt-decode';

import styles from './commons/styles/project-style.css';
import DeviceAdminPage from "./admin/device-admin-page";
import ClientDashboard from './client/client-dashboard';

class App extends React.Component {

    render() {
        return (
            <div className={styles.back}>
                <Router>
                    <div>
                        <NavigationBar />
                        <Switch>

                            {/* RUTA DEFAULT */}
                            <Route exact path='/' render={() => {
                                const token = localStorage.getItem("token");
                                if (!token) {
                                    return <Redirect to="/login" />;
                                }

                                try {
                                    const decoded = jwtDecode(token);
                                    const role = decoded.role || (decoded.roles && decoded.roles[0]);

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

                            {/* RUTA DE CLIENT */}
                            <Route exact path='/client' render={() => {
                                if (!localStorage.getItem("token")) return <Redirect to="/login" />;
                                return <ClientDashboard/>;
                            }}/>

                            {/* RUTE ADMIN */}
                            <Route exact path='/admin' render={() => {
                                if (!localStorage.getItem("token")) return <Redirect to="/login" />;
                                return <AdminDashboard/>; // Asta e pagina de useri default
                            }}/>

                            {/* RUTA NOUA PENTRU DEVICE-URI */}
                            <Route exact path='/admin/devices' render={() => {
                                if (!localStorage.getItem("token")) return <Redirect to="/login" />;
                                return <DeviceAdminPage/>;
                            }}/>

                            <Route exact path='/client' render={() => <h1>Pagina Client (In lucru)</h1>} />

                            <Route component={ErrorPage} />
                        </Switch>
                    </div>
                </Router>
            </div>
        )
    };
}

export default App;