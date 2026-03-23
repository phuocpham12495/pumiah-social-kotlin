-- Allow users to delete their own notifications
CREATE POLICY "Users can delete own notifications"
ON notifications
FOR DELETE
USING (recipient_id = auth.uid());
