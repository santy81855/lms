import {render, screen} from '@testing-library/react';
import userEvent from "@testing-library/user-event";
import { MemoryRouter, replace } from 'react-router';
import { LandingPage } from '../LandingPage';
import * as authHook from '@/features/auth/hooks/useAuth';
import type { LoginRequest, AuthUser, RegisterRequest, UserRole } from '@/features/auth';
import { MockAuthBuilder } from './MockAuthBuilder';

// mock the useNavigate hook
const mockNavigate = vi.fn();
vi.mock('react-router', async (importOriginal) => {
    const actual = await importOriginal<typeof import('react-router')>();
    return {...actual, useNavigate: () => mockNavigate};
});

// mock useAuth
vi.mock('@/features/auth/hooks/useAuth');
let mockUseAuth = vi.spyOn(authHook, 'useAuth');

describe('LandingPage', () => {

    describe('when user is not authenticated', () => {
        beforeEach(() => {
            mockUseAuth.mockReturnValue(MockAuthBuilder
                .newMock()
                .nullUser()
                .build());
        });

        it('shows login and register links' /*or else it gets the hose again*/ , () => {
            renderLandingPage();
            expect(screen.getByRole('link', { name: 'Log in' })).toBeInTheDocument();
            expect(screen.getByRole('link', { name: 'Create account' })).toBeInTheDocument();
        });

        it('shows get started and login links in hero', () => {
            renderLandingPage();
            expect(screen.getByRole('link', { name: 'Get started' })).toBeInTheDocument();
            expect(screen.getByRole('link', { name: 'I already have an account' })).toBeInTheDocument();
        });

        it('does not show logout button', () => {
            renderLandingPage();
            expect(screen.queryByRole('button', { name: 'Log out' })).not.toBeInTheDocument();
        });

        it('can navigate to the register page', async () => {
            renderLandingPage();
            const registerLinks = screen.getAllByRole('link', {name: /Create account/i});
            registerLinks.forEach(link => expect(link).toHaveAttribute('href', '/register'));
        })
    });

    describe('when user is authenticated', () => {
        beforeEach(() => {
            
            mockUseAuth.mockReturnValue(MockAuthBuilder
                .newMock()
                .authenticatePerson('Jane')
                .isStudent()
                .build());
        });

        it('shows greeting with first name', () => {
            renderLandingPage();
            expect(screen.getByText('Hi, Jane')).toBeInTheDocument();
        });

        it('shows logout button and dashboard link', () => {
            renderLandingPage();
            expect(screen.getByRole('button', { name: 'Log out' })).toBeInTheDocument();
            expect(screen.getByRole('link', { name: 'Dashboard' })).toBeInTheDocument();
        });

        it('calls logout and navigates home on logout click', async () => {
            const mockLogout = vi.fn().mockResolvedValue(undefined);
            mockUseAuth.mockReturnValue(MockAuthBuilder
                .newMock()
                .authenticatePerson("Bob")
                .isStudent()
                .injectLogoutFunction(mockLogout)
                .build()
            )

            renderLandingPage();
            await userEvent.click(screen.getByRole('button', { name: 'Log out' }));

            expect(mockLogout).toHaveBeenCalledOnce();
            expect(mockNavigate).toHaveBeenCalledWith('/', { replace: true });
        });
    });

    describe('role-based routing', () => {
        it('points dashboard links to /teacher for teachers', () => {
            mockUseAuth.mockReturnValue(MockAuthBuilder
                .newMock()
                .authenticatePerson("Bob")
                .isTeacher()
                .build()
            );

            renderLandingPage();
            const dashboardLinks = screen.getAllByRole('link', { name: /dashboard/i });
            dashboardLinks.forEach(link => {
                expect(link).toHaveAttribute('href', '/teacher');
            });
        });

        it('points dashboard links to /student for students', () => {
            mockUseAuth.mockReturnValue(
                MockAuthBuilder
                .newMock()
                .authenticatePerson("Jane")
                .isStudent()
                .build()
            );

            renderLandingPage();
            const dashboardLinks = screen.getAllByRole('link', { name: /dashboard/i });
            dashboardLinks.forEach(link => {
                expect(link).toHaveAttribute('href', '/student');
            });
        });
    });
});

function renderLandingPage(){
    return render(
        <MemoryRouter>
            <LandingPage />
        </MemoryRouter>
    );
}