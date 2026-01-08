import React, { useState } from 'react';
import {
    Nav,
    Navbar,
    NavbarBrand,
    NavbarToggler,
    Collapse,
    NavItem,
    NavLink,
    Button,
    Container
} from 'reactstrap';
import { jwtDecode } from 'jwt-decode';


const navbarStyle = {
    backgroundColor: '#ffffff',
    boxShadow: '0 2px 15px rgba(0, 0, 0, 0.05)', // Umbra fina
    padding: '15px 0',
    zIndex: '1000'
};

const brandStyle = {
    fontWeight: '800',
    color: '#1e293b',
    fontSize: '1.5rem',
    letterSpacing: '-0.5px'
};

const linkStyle = {
    color: '#64748b',
    fontWeight: '600',
    fontSize: '0.95rem',
    marginLeft: '15px',
    transition: 'color 0.2s',
    cursor: 'pointer'
};

const activeLinkStyle = {
    color: '#2563eb',
    fontWeight: '700'
};

const NavigationBar = () => {
    const [isOpen, setIsOpen] = useState(false);
    const toggle = () => setIsOpen(!isOpen);

    const token = localStorage.getItem("token");
    const isAuthenticated = token !== null;
    const username = localStorage.getItem("username");

    let isAdmin = false;
    if (isAuthenticated) {
        try {
            const decoded = jwtDecode(token);
            if (decoded.role === 'ADMIN' || (decoded.roles && decoded.roles.includes('ADMIN'))) {
                isAdmin = true;
            }
        } catch (e) {
            console.error("Token invalid:", e);
        }
    }

    const handleLogout = () => {
        localStorage.clear();
        window.location.href = "/login";
    };

    return (
        <div>
            {}
            <Navbar color="white" light expand="md" style={navbarStyle}>
                <Container fluid={true} style={{ paddingLeft: '30px', paddingRight: '30px' }}>

                    {}
                    <NavbarBrand href="/" style={brandStyle}>
                        Energy<span style={{ color: '#2563eb' }}>Platform</span>
                    </NavbarBrand>

                    {}
                    <NavbarToggler onClick={toggle} style={{ border: 'none' }} />

                    {}
                    <Collapse isOpen={isOpen} navbar>

                        {}
                        <Nav className="mr-auto" navbar style={{ marginLeft: '20px' }}>
                            {isAuthenticated && isAdmin && (
                                <>
                                    <NavItem>
                                        <NavLink href="/admin" style={linkStyle}>
                                            Users
                                        </NavLink>
                                    </NavItem>
                                    <NavItem>
                                        <NavLink href="/admin/devices" style={linkStyle}>
                                            Devices
                                        </NavLink>
                                    </NavItem>
                                </>
                            )}
                        </Nav>

                        {}
                        {isAuthenticated && (
                            <Nav className="ml-auto align-items-center" navbar>
                                <NavItem className="d-flex align-items-center mr-3">
                                    <span style={{ color: '#94a3b8', fontSize: '0.9rem', fontWeight: '500', marginRight: '5px' }}>
                                        Signed in as
                                    </span>
                                    <span style={{ color: '#1e293b', fontWeight: '700' }}>
                                        {username}
                                    </span>
                                </NavItem>

                                <NavItem>
                                    <Button
                                        outline
                                        color="danger"
                                        size="sm"
                                        onClick={handleLogout}
                                        style={{
                                            borderRadius: '6px',
                                            fontWeight: '600',
                                            fontSize: '0.85rem',
                                            marginLeft: '15px',
                                            borderWidth: '2px'
                                        }}
                                    >
                                        Log Out
                                    </Button>
                                </NavItem>
                            </Nav>
                        )}

                    </Collapse>
                </Container>
            </Navbar>
        </div>
    );
};

export default NavigationBar;