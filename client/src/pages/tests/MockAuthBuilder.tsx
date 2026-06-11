import type { AuthContextValue } from '@/features/auth/context/authContext';
import * as authHook from '@/features/auth/hooks/useAuth';
import type { LoginRequest, AuthUser, RegisterRequest, UserRole } from '@/features/auth';
import type { Mock } from '@vitest/spy';




export class MockAuthBuilder{

    private auth: AuthContextValue = {
        user: null,
        roles: [],
        isLoading: false,
        isAuthenticated: false,
        login: function (data: LoginRequest): Promise<AuthUser> {
            throw new Error('Function not implemented.');
        },
        registerStudent: function (data: RegisterRequest): Promise<AuthUser> {
            throw new Error('Function not implemented.');
        },
        registerTeacher: function (data: RegisterRequest): Promise<AuthUser> {
            throw new Error('Function not implemented.');
        },
        logout: function (): Promise<void> {
            throw new Error('Function not implemented.');
        },
        refreshCurrentUser: function (): Promise<void> {
            throw new Error('Function not implemented.');
        },
        hasRole: function (role: UserRole): boolean {
            throw new Error('Function not implemented.');
        }
    }

    public static newMock(): MockAuthBuilder{
        return new MockAuthBuilder();
    }

    public authenticatePerson(name: string): MockAuthBuilder{
        this.auth.user = {
            id: 0,
            firstName: name,
            lastName: '',
            email: '',
            accountStatus: '',
            roles: []
        }
        this.auth.isAuthenticated = true;

        return this;
    }

    public injectLogoutFunction(operation: () => Promise<void>): MockAuthBuilder{
        this.auth.logout = operation;
        return this;
    }

    public nullUser(): MockAuthBuilder{
        this.auth.user = null;
        this.auth.hasRole = function(role: UserRole): boolean{
            return false;
        }
        return this;
    }
    
    public isStudent(): MockAuthBuilder{
        if(this.auth.user === null){
            this.auth.user = {
            id: 0,
            firstName: "",
            lastName: '',
            email: '',
            accountStatus: '',
            roles: []
            }
        }

        this.auth.user.roles = ["STUDENT"];
        this.auth.hasRole = function(role: UserRole): boolean{
            return role === "STUDENT";
        }

        return this;
    }

    public isTeacher(): MockAuthBuilder{
        if(this.auth.user === null){
            this.auth.user = {
            id: 0,
            firstName: "",
            lastName: '',
            email: '',
            accountStatus: '',
            roles: []
            }
        }
        
        this.auth.user.roles = ["TEACHER"];
        this.auth.hasRole = function(role: UserRole): boolean{
            return role === "TEACHER";
        }
        return this;
    }

    public build(): AuthContextValue{
        return this.auth;
    }
}