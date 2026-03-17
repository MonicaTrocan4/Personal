import React from 'react';
import {Container, Jumbotron, Button} from 'reactstrap';
import {jwtDecode} from 'jwt-decode';

class Home extends React.Component {

    componentDidMount() {
        const token = localStorage.getItem("token");

        if (!token) {
            return;
        }

        let role = "CLIENT";
        try {
            const decoded = jwtDecode(token);
            if (decoded.roles && decoded.roles.includes("ADMIN")) {
                role = "ADMIN";
            }
        } catch(e) {
            console.log("Token invalid");
        }

        if (role === 'ADMIN') {
            window.location.href = "/admin";
        } else {
            window.location.href = "/client";
        }
    }

    render() {
        return (
            <div>
                <Jumbotron fluid>
                    <Container fluid>
                        <h1 className="display-3">Energy Management System</h1>
                        <p className="lead">Bine ai venit!</p>
                        <hr className="my-2" />
                        <p>Te rugăm să te loghezi pentru a accesa platforma.</p>
                        <p className="lead">
                            <Button color="primary" href="/login">Login</Button>
                        </p>
                    </Container>
                </Jumbotron>
            </div>
        );
    }
}

export default Home;