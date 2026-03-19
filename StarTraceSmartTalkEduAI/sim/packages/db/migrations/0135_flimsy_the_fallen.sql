CREATE TYPE "public"."course_role" AS ENUM('teacher', 'student', 'admin');--> statement-breakpoint
CREATE TABLE "course" (
	"id" text PRIMARY KEY NOT NULL,
	"name" text NOT NULL,
	"description" text,
	"instructor_id" text,
	"workspace_id" text NOT NULL,
	"created_at" timestamp DEFAULT now() NOT NULL,
	"updated_at" timestamp DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "course_member" (
	"id" text PRIMARY KEY NOT NULL,
	"course_id" text NOT NULL,
	"user_id" text NOT NULL,
	"role" "course_role" DEFAULT 'student' NOT NULL,
	"joined_at" timestamp DEFAULT now() NOT NULL
);
--> statement-breakpoint
ALTER TABLE "course" ADD CONSTRAINT "course_instructor_id_user_id_fk" FOREIGN KEY ("instructor_id") REFERENCES "public"."user"("id") ON DELETE set null ON UPDATE no action;--> statement-breakpoint
ALTER TABLE "course" ADD CONSTRAINT "course_workspace_id_workspace_id_fk" FOREIGN KEY ("workspace_id") REFERENCES "public"."workspace"("id") ON DELETE cascade ON UPDATE no action;--> statement-breakpoint
ALTER TABLE "course_member" ADD CONSTRAINT "course_member_course_id_course_id_fk" FOREIGN KEY ("course_id") REFERENCES "public"."course"("id") ON DELETE cascade ON UPDATE no action;--> statement-breakpoint
ALTER TABLE "course_member" ADD CONSTRAINT "course_member_user_id_user_id_fk" FOREIGN KEY ("user_id") REFERENCES "public"."user"("id") ON DELETE cascade ON UPDATE no action;--> statement-breakpoint
CREATE INDEX "course_instructor_id_idx" ON "course" USING btree ("instructor_id");--> statement-breakpoint
CREATE INDEX "course_workspace_id_idx" ON "course" USING btree ("workspace_id");--> statement-breakpoint
CREATE INDEX "course_workspace_instructor_idx" ON "course" USING btree ("workspace_id","instructor_id");--> statement-breakpoint
CREATE INDEX "course_member_course_id_idx" ON "course_member" USING btree ("course_id");--> statement-breakpoint
CREATE INDEX "course_member_user_id_idx" ON "course_member" USING btree ("user_id");--> statement-breakpoint
CREATE INDEX "course_member_course_user_idx" ON "course_member" USING btree ("course_id","user_id");--> statement-breakpoint
CREATE INDEX "course_member_role_idx" ON "course_member" USING btree ("role");--> statement-breakpoint
CREATE UNIQUE INDEX "course_member_course_user_unique" ON "course_member" USING btree ("course_id","user_id");