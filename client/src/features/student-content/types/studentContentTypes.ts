import type { ModuleContentItem } from "@/features/teacher-content";
import type { CourseModule } from "@/features/teacher-modules";

export type StudentModuleWithContent = {
    module: CourseModule;
    contentItems: ModuleContentItem[];
};
